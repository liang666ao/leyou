package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.entity.PageResult;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(Spu spu){
        Long spuId = spu.getId();
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException((ExceptionEnum.CATEGROY_CANNOT_BE_NULL));
        }
        //获取分类名称
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null){
            throw new LyException(ExceptionEnum.BRAND_CANNOT_BE_NULL);
        }
        // 处理all
        String all = spu.getTitle()+ StringUtils.join(names," ")+spu.getTitle();
        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException((ExceptionEnum.GOODS_CANNOT_BE_NULL));
        }
        //对sku进行处理
        List<Map<String,Object>> skus = new ArrayList<>();
        Set<Long> prices = new HashSet<>();
        for (Sku sku : skuList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            prices.add(sku.getPrice());

        }
        // 查询规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)){
            throw new LyException((ExceptionEnum.SPECGROUNP_NOT_FOND));
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        // 处理通用属性
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        // 处理特殊属性
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});

        // 规格参数,key是规格参数的名字,值是规格参数的值
        Map<String,Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            //规格名称
            String key = param.getName();
            Object value = "";
            //判断是否是通用规格
            if (param.getGeneric()){
                value = genericSpec.get(param.getId());
                //判断是否是数值类型
                if(param.getNumeric()){
                    //处理成段
                    value = chooseSegment(value.toString(), param);
                }
            }else {
                value = specialSpec.get(param.getId());
            }
            //存入map
            specs.put(key,value);
        }

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);
        goods.setPrice(prices);
        goods.setSkus(JsonUtils.toString(skus));
        goods.setSpecs(specs);

        return goods;
    }

    private String chooseSegment(String value,SpecParam p){
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        //保存数值段
        for (String segment : p.getSegments().split(",")){
            String[] segs = segment.split("-");
            //获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end){
                if (segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if (begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        String key  = request.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null,不允许搜索全部商品
        if(StringUtils.isBlank(key)){
            return null;
        }
        Integer page = request.getPage()-1;
        Integer size = request.getSize();
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //0 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //1 分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //2 过滤
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));
        //3 查询
        Page<Goods> result = goodsRepository.search(queryBuilder.build());
        //4 解析结果
        long total = result.getTotalElements();
        long totalPage = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        return new PageResult<>(total,totalPage,goodsList);
    }
}
