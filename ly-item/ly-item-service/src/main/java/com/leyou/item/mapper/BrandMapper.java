package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 处理品牌与分类中间表
     * @param cid
     * @param bid
     * @return
     */
    @Insert("insert into tb_category_brand values(#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid")Long cid,@Param("bid") Long bid);

    /**
     * 删除品牌对应的分类
     * @return
     */
    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    int deleteCategoryBrand(Long bid);

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @Select("SELECT b.* FROM tb_brand b LEFT JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryBrandByCid(Long cid);

}