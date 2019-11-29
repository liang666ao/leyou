package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.entity.PageResult;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;


    /**
     * 商品信息查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        //1.开启分页
        PageHelper.startPage(page,rows);
        //2.搜索过滤
        Example example=new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();

        //3.关键字过滤
        if(StringUtils.isNoneBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //4.上下架
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //5.最后修改时间降序排列
        example.orderBy("lastUpdateTime").desc();
        List<Spu> spus = spuMapper.selectByExample(example);
        if(spus==null){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }

        //6.处理商品分类和品牌信息
        bindCategoryAndBrand(spus);


        PageInfo<Spu> info=new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(),spus);
    }

    /**
     * 绑定分类和品牌信息
     * @param spus
     */
    private void bindCategoryAndBrand(List<Spu> spus) {
        for (Spu spu : spus) {
            //根据主键批量查找分类信息
            String cname=categoryService.queryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.joining("/"));
            spu.setCname(cname);
            //查询品牌
            String bname=brandService.queryBrandById(spu.getBrandId()).getName();
            spu.setBname(bname);
        }
    }

    public void saveGoods(Spu spu) {
        //新增Spu
        spu.setCreateTime(new Date());
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setLastUpdateTime(new Date());
        int count=spuMapper.insertSelective(spu);
        if (count==0){
            throw new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
        //新增Spu_Detail
        SpuDetail detail=spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        spuDetailMapper.insert(detail);
        //新增sku
        for (Sku sku : spu.getSkus()) {
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            count=skuMapper.insert(sku);
            if (count==0){
                throw new LyException(ExceptionEnum.SPECGROUNP_ERROR);
            }
            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            count=stockMapper.insertSelective(stock);
            if (count==0){
                throw new LyException(ExceptionEnum.SPECGROUNP_ERROR);
            }
        }



    }

    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail detail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (detail == null ){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }
        return detail;
    }

    public List<Sku> querySkuBySpuId(Long supId) {
        //查询sku
        Sku sku = new Sku();
        sku.setSpuId(supId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }
        // 查询库存
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }
        // 我们把stock变成一个map,其key是:sku的id，值是库存值
        Map<Long , Integer> stockMap = stockList.stream()
                .collect(Collectors.toMap(Stock::getSkuId,Stock::getStock));
        skuList.forEach(s->s.setStock(stockMap.get(s.getId())));
        return skuList;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        if (spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }
        deleteSkuAndStock(spu);
        // 修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }
        //修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count != 1){
            throw new LyException(ExceptionEnum.GOODS_CANNOT_BE_NULL);
        }
        //新增sku和stock
        saveSkuAndStock(spu);
    }

    private void saveSkuAndStock(Spu spu) {
        spu.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 删除方法
     * @param id
     */
    @Transactional
    public void deleteGoods(Long id) {
        Spu spu=spuMapper.selectByPrimaryKey(id);
        //删除sku与stock
        deleteSkuAndStock(spu);
        //删除商品详细
        int count = spuDetailMapper.deleteByPrimaryKey(spu.getId());
        if (count==0){
            throw new LyException(ExceptionEnum.GOODS_ERROR);
        }
        //删除商品
        count=spuMapper.deleteByPrimaryKey(id);
        if (count==0){
            throw new LyException(ExceptionEnum.GOODS_ERROR);
        }

    }

    private void deleteSkuAndStock(Spu spu) {
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            //删除sku
            int count=skuMapper.delete(sku);
            if (count==0){
                throw new LyException(ExceptionEnum.GOODS_ERROR);
            }
            //删除stock
            count=stockMapper.deleteByIdList(ids);
            if (count==0){
                throw new LyException(ExceptionEnum.GOODS_ERROR);
            }
        }
    }

    public void updateSaleAble(Long id, Boolean saleable) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);
        spuMapper.updateByPrimaryKeySelective(spu);


    }
}
