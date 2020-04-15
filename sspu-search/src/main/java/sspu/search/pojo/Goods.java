package com.sspu.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**封装要保存到索引库的数据，并设置映射属性：
 * @author hp
 *all：用来进行全文检索的字段，里面包含标题、商品分类信息
 *  price：价格数组，是所有sku的价格集合。方便根据价格进行筛选过滤
 *  skus：用于页面展示的sku信息，不索引，不搜索。包含skuId、image、price、title字段
 *  specs：所有规格参数的集合。key是参数名，值是参数值。
 *
 */
@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {
    @Id
    private Long id; // spuId
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;// 卖点
    private Long brandId;// 品牌id
    private Long cid1;// 1级分类id
    private Long cid2;// 2级分类id
    private Long cid3;// 3级分类id
    private Date createTime;// 创建时间
    private List<Long> price;// 价格
    @Field(type = FieldType.Keyword, index = false)
    private String skus;// List<sku>信息的json结构
    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值
}
