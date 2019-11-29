package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.entity.PageResult;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public Brand queryById(Long id){
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询品牌信息
     * @return
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc){
        //1.分页
        PageHelper.startPage(page,rows);
        Example example=new Example(Brand.class);
        Example.Criteria criteria=example.createCriteria();
        //2.关键字过滤
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("letter","%"+key+"%");
        }
        //3.排序
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause( sortBy + (desc?" desc":" asc"));
        }
        //4.获取列表
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_CANNOT_BE_NULL);
        }
        PageInfo<Brand> pages=new PageInfo<>(brands);
        return new PageResult<>(pages.getTotal(),brands);
    }

    public Brand queryBrandById(Long id){
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        //1.新增品牌
        int count = brandMapper.insert(brand);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        addBrandCategory(brand.getId(), cids);
    }

    private void addBrandCategory(Long bid, List<Long> cids) {
        int count;//2.新增中间表
        for (Long cid : cids) {
            count=brandMapper.insertCategoryBrand(cid,bid);
            if(count==0){
                throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
            }
        }
    }

    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        //1.修改品牌
        int count = brandMapper.updateByPrimaryKey(brand);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        //2.删除品牌对应的分类信息
        count=brandMapper.deleteCategoryBrand(brand.getId());
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        //3.新增
        addBrandCategory(brand.getId(), cids);
    }

    @Transactional
    public void deleteBrand(Long id) {
        //1.删除品牌
        int count = brandMapper.deleteByPrimaryKey(id);
        if(count==0){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        //2.删除品牌对应的分类信息
        count=brandMapper.deleteCategoryBrand(id);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }

    }

    public List<Brand> queryBrandByCid(Long cid){
        List<Brand> list = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        return list;
    }
}
