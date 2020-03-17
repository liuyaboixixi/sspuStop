package com.leyou.item.api;


import com.leyou.item.SpecGroup;
import com.leyou.item.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {
    /**
     * g根据查询条件gid 查询规格参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public List<SpecParam>
    queryParams(@RequestParam(value = "gid", required = false) Long gid,
                @RequestParam(value = "cid", required = false) Long cid,
                @RequestParam(value = "generic", required = false) Boolean generic,
                @RequestParam(value = "searching", required = false) Boolean searching

    );

    /** 通过cid  查出所有的组，在通过组id查出所有的规格参数
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid") Long cid);
}
