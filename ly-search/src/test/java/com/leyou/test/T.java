package com.leyou.test;

import com.leyou.item.pojo.Category;
import com.leyou.search.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class T {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void test(){
        List<Category> categories=categoryClient.queryCategoryByIds(Arrays.asList(1L,2L,3L));
        categories.forEach(System.out::println);
    }

}
