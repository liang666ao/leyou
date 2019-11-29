package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据id批量查询
     * @param ids
     * @return
     */
    public List<Category> queryByIds(List<Long> ids){
        return categoryMapper.selectByIdList(ids);
    }

    public List<Category> queryCategoryByPid(Long pid) {
        Category category=new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGROY_CANNOT_BE_NULL);
        }
        return list;
    }

    public List<Category> queryByBrandId(Long bid) {
        List<Category> categories = categoryMapper.queryBrandById(bid);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGROY_CANNOT_BE_NULL);
        }
        return categories;


    }
}
