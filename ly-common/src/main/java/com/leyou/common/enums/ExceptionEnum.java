package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public enum  ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGROY_CANNOT_BE_NULL(400,"未查到分类信息"),
    BRAND_CANNOT_BE_NULL(400,"未查到品牌信息"),
    BRAND_EDIT_ERROR(500,"品牌信息操作失败"),
    UPLOAD_TYPE_ERROR(500,"图片格式或内容校验失败"),
    UPLOAD_ERROR(500,"图片上传失败"),
    SPECGROUNP_NOT_FOND(400,"规格参数查询失败"),
    SPECGROUNP_ERROR(500,"规格参数操作失败"),
    GOODS_CANNOT_BE_NULL(400,"未查到商品信息"),
    GOODS_ERROR(500,"商品操作失败"),
    ;
    private int code;
    private String msg;
}
