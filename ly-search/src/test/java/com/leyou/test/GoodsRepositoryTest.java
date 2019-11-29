package com.leyou.test;

import com.leyou.common.entity.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;

    @Test
    public void create_test(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void save_test(){
        int page = 1;//页码
        int rows = 100;//每页条数
        int size = 0;//每次取出条数
        do{
            PageResult<Spu> pages = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> spus = pages.getItems();
            /*for (Spu spu : spus) {
                Goods goods = searchService.buildGoods(spu);
                goodsRepository.save(goods);
            }*/
            List<Goods> goods = spus.stream().map(searchService::buildGoods).collect(Collectors.toList());
            goodsRepository.saveAll(goods);
            page++;
            size=spus.size();
        }while (size==rows);


    }

    @Test
    public void Test03(){
        Integer f1=100,f2=100,f3=150,f4=150;
        System.out.println(f1==f2);
        System.out.println(f3==f4);
    }


}