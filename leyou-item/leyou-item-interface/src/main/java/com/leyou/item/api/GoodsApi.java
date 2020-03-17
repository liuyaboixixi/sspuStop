package com.leyou.item.api;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.PageResult;
import com.leyou.item.Sku;
import com.leyou.item.Spu;
import com.leyou.item.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping
public interface GoodsApi {
    /**
     * 根据条件查询分页
     *
     * @param key      搜索条件
     * @param saleable 状态：  全部  上架  下架
     * @param page     当前页
     * @param rows     一页有多少行数据
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );


    /**
     * 根据spuId查询spuDetail  商品详情
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")                       //PathVariable专门是用来路径的站位符
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);


    /**分析

     - 请求方式：Get
     - 请求路径：/sku/list
     - 请求参数：id，应该是spu的id
     - 返回结果：sku的集合
     根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    /**根据spu  id  来查询spu
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Spu querySkuBySpuId(@PathVariable("id")Long id);

//**根据sku  id  来查询sku
    @GetMapping("sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId") Long skuId);
}
