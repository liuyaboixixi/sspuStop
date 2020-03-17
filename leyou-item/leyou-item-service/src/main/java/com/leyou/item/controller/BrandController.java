package com.leyou.item.controller;

import com.leyou.item.Brand;
import com.leyou.item.pojo.PageResult;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
品牌功能Controller
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc
    ) {
        PageResult<Brand> result = brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * @param brand
     * @param cids
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.PUT,RequestMethod.GET})
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam(value = "cids") List<Long> cids) {
        Brand brand1=brandService.queryBrandsById(brand.getId());
        if (brand==null){
            return ResponseEntity.notFound().build();
        }else if (brand1==null){
            brandService.saveBrand(brand, cids);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else {
            int i = brandService.updateBrand(brand);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**根据分类查询品牌列表
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid")Long cid){
        List<Brand> brands=brandService.queryBrandsByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }
    /**根据分类查询品牌列表
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandsById(@PathVariable("id")Long id){
        Brand brand=brandService.queryBrandsById(id);
       if (brand==null){
           return ResponseEntity.notFound().build();
       }
        return ResponseEntity.ok(brand);
    }
}
