package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecParam;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface SpecParamMapper extends Mapper<SpecParam>, DeleteByIdListMapper<SpecParam,Long> {
}
