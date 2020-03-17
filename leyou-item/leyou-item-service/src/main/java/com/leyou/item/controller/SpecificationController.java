package com.leyou.item.controller;

import com.leyou.item.SpecGroup;
import com.leyou.item.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    SpecificationService specificationService;

    /**
     * 根据分类id查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
    /**
     * 根据分类id查询分组
     *
     * @param cid
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<List<SpecGroup>> saveGroups(@RequestBody SpecGroup specGroup,
                                                      @RequestParam(value = "cid",required = false) Long cid,
                                                      @RequestParam(value = "name",required = false) String name) {


        int i = this.specificationService.saveGroup(specGroup);
        if (i!=0){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * g根据查询条件gid 查询规格参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>>
    queryParams(@RequestParam(value = "gid", required = false) Long gid,
                @RequestParam(value = "cid" ,required = false) Long cid,
                @RequestParam(value = "generic", required = false) Boolean generic ,
                @RequestParam(value = "searching", required = false) Boolean searching

                ) {

        List<SpecParam> params = specificationService.queryParams(gid,cid,generic,searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid)
    {
        List<SpecGroup> groups=this.specificationService.querySpecsByCid(cid);
         if (CollectionUtils.isEmpty(groups)){
             return  ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(groups);
    }

}
