package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid){
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> groups = specGroupMapper.select(specGroup);
        if(CollectionUtils.isEmpty(groups)){
            throw  new LyException(ExceptionEnum.SPECGROUNP_NOT_FOND);
        }
        return groups;
    }


    public List<SpecParam> queryParamByGid(Long gid, Long cid){
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        List<SpecParam> list = specParamMapper.select(param);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
        return list;
    }

    public void saveSpecGroup(SpecGroup group) {
        int count = specGroupMapper.insert(group);
        if (count==0){
            throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
    }

    public void updateSpecGroup(SpecGroup group) {
        int count =  specGroupMapper.updateByPrimaryKeySelective(group);
        if (count==0){
            throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
    }

    public void saveSpecParam(SpecParam param) {
        int count = specParamMapper.insert(param);
        if (count==0){
            throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
    }

    public void updateSpecParam(SpecParam param) {
        int count = specParamMapper.updateByPrimaryKeySelective(param);
        if (count==0){
            throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
    }

    public void deleteSpecParam(Long pid) {
        int count = specParamMapper.deleteByPrimaryKey(pid);
        if (count==0){
            throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
    }

    @Transactional
    public void deleteSpecGroup(Long gid) {
        //1.先删除规格参数t
        List<SpecParam> params = this.queryParamByList(gid,null,null);
        if(!(CollectionUtils.isEmpty(params))){
            /*for (SpecParam param : params) {
                int count = specParamMapper.deleteByPrimaryKey(param.getId());
                if (count==0){
                    throw  new LyException(ExcepionEnum.SPECGROUNP_ERROR);
                }
            }*/
            //根据id批量删除
            List<Long> ids = params.stream().map(SpecParam::getId).collect(Collectors.toList());
            int count= specParamMapper.deleteByIdList(ids);
            if (count==0){
                throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
            }
        }
        //2.删除分组
        int count=specGroupMapper.deleteByPrimaryKey(gid);
        if (count==0){
            throw  new LyException(ExceptionEnum.SPECGROUNP_ERROR);
        }
    }

    public List<SpecParam> queryParamByList(Long gid, Long cid,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        return specParamMapper.select(specParam);
    }
}
