package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spu_detail")
public class SpuDetail {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long spuId;
    private String description; // 商品描述信息
    private String genericSpec; // 通用规格参数数据
    private String specialSpec; // 特有规格参数及可选值信息，json格式
    private String packingList; // 包装清单
    private String afterService; // 售后服务
}
