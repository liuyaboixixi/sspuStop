package com.leyou.item.bo;

import com.leyou.item.Sku;
import com.leyou.item.Spu;
import com.leyou.item.SpuDetail;
import lombok.Data;

import java.util.List;

/**请求参数：Spu的json格式的对象，spu中包含spuDetail和Sku集合。这里我们该怎么接收？我们之前定义了一个SpuBo对象
 * ，作为业务对象。这里也可以用它，不过需要再扩展spuDetail和skus字段：
 * @author hp
 */
@Data
public class SpuBo extends Spu {
    private String cname; //商品分类名称
    private String bname; //品牌名称
    private SpuDetail spuDetail;  //商品详情
    private List<Sku> skus;//sku列表

}
