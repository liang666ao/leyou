package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类信息查询商品规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable Long cid){

        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 根据分组查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "searching",required = false) Boolean searching,
            @RequestParam(value = "cid",required = false) Long cid){
        return ResponseEntity.ok(specificationService.queryParamByList(gid,cid,searching));
    }

    /**
     * 新增分组  RequestBody 请求主体接收对象
     * @param group
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup group){
        specificationService.saveSpecGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup group){
        specificationService.updateSpecGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 新增参数
     * @param param
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecParam param){
        specificationService.saveSpecParam(param);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("param")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecParam param){
        specificationService.updateSpecParam(param);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("param/{pid}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable Long pid){
        specificationService.deleteSpecParam(pid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("group/{gid}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable Long gid){
        specificationService.deleteSpecGroup(gid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
