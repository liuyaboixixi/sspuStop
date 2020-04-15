package com.sspu.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.*;
import com.sspu.search.client.BrandClient;
import com.sspu.search.client.CategoryClient;
//import leyou.search.client.GoodsClient;
//import leyou.search.client.SpecificationClient;
//import leyou.search.pojo.Goods;
//import leyou.search.pojo.SearchRequest;
//import leyou.search.pojo.searchResult;
//import leyou.search.repository.GoodsRepository;
import com.sspu.search.client.GoodsClient;
import com.sspu.search.client.SpecificationClient;
import com.sspu.search.pojo.Goods;
import com.sspu.search.pojo.SearchRequest;
import com.sspu.search.pojo.searchResult;
import com.sspu.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    GoodsRepository goodsRepository;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        //设置好goods的参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());

        //拼接all字段，需要  标题   分类名称以及品牌名称
        //1.根据分类的ID查询分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //2.根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());


        goods.setAll(spu.getSubTitle() + " " + StringUtils.join(names, " ") + " " + brand.getName());
        //获取spu下的所有sku的价格
        ArrayList<Long> prices = new ArrayList<>();
        //先查出所有的spus
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());

        List<Map<String, Object>> skuMapList = new ArrayList<>();//手机sku的必要属性
        //获取spu下的所有sku  并转化成json  字符串
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            HashMap<String, Object> skuMap = new HashMap<>();//存放sku属性
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            //获取sku中的图片，数据库中的图片可能是多张，段长以“，”分割，也是以“，”来进行分割
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ?
                    StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });
        //利用 Jackson  把skumaplist 转化为  json
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //把所有价格
        goods.setPrice(prices);

        //获取所有查询的规格参数{name:value}
        //1.根据spu cid3查出所有的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        //根据spuid 查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值，进行反序列化  MAP
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //把商品特殊的规格参数，进行反序列化 map
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });


        Map<String, Object> specs = new HashMap<>();

        //*
        params.forEach(param -> {
            //1.首先要判断规格参数的类型，是否是通用的规格参数
            if (param.getGeneric()) {
                //如果是通用的规格参数，从genericSpecMap获取值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //如果通用的规格参数，是数字类型，应该返回一个区间
                if (param.getGeneric()){
                    value=chooserSegment(value,param);
                }
                specs.put(param.getName(),value);
            }else {
                //如果是特殊的规格参数，就从specialSpecMap  获取值
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(),value);
            }
        });
        goods.setSpecs(specs);
        return goods;
    }
    //因为过滤参数中有一类比较特殊，就是数值区间：
    private String chooserSegment(String value, SpecParam param) {
        double val = NumberUtils.toDouble(value);
        String result = "其他";
        //保存数组段
        for (String segment : param.getSegments().split(",")) {
            String[] segs = segment.split("-");
            //获取数值的范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            //判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + param.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + param.getUnit() + "以下";
                } else {
                    result = segment + param.getUnit();
                }
                break;
            }
        }
        return result;
    }


    /**从elasticsearch 中间件中查询 所需要的商品
     * @param searchRequest
     * @return
     */
    public searchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        //判断是否有搜索条件，如果没有，直接返回null.不允许搜索全部商品
        if (StringUtils.isBlank(key)){
            return null;
        }
        //自定义查询构建器   构建查询条件    NativeSearchQueryBuilder：Spring提供的一个查询条件构建器，
        // 帮助构建json格式的请求体
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
           //1.对key进行全文检索查询
       // QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);//查询条件
        BoolQueryBuilder basicQuery=buildBoolQueryBuilder(searchRequest);
        queryBuilder.withQuery(basicQuery);
            //2.通过sourceFilter设置返回的结果字段，我们只需要id skus subtitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String []{"id","skus","subTitle"},null));
            //3 分页
            //3.1准备分页参数
        int page = searchRequest.getPage();
        int size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));

        /*
        添加分类和品牌的聚合，为了在面包屑那一部分  使用同一个接口，让此接口更加的复用性，
        所谓聚合就是把  一些数据进行分析， 比如排序  分析等
         */
        String categoryAggName="categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));


        //4.查询，获取结果
        /*
        AggregatedPage  不仅有普通的结果集，还有聚合的结果集
         */
        Page<Goods> search = (Page<Goods>) this.goodsRepository.search(queryBuilder.build());

        /*
        获得聚合结果集
         */
        List<Map<String,Object>> categories= getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands= getBrandsAggResult(goodsPage.getAggregation(brandAggName));
        /*****************************判断分类分类的结果集大小，如果为1  进行规格聚合***************************************/
        List<Map<String,Object>> specs=null;
        if (!CollectionUtils.isEmpty(categories)&&categories.size()==1){
            //对规格参数进行聚合
             specs=getParamAggResult((Long)categories.get(0).get("id"),basicQuery);
        }
        //封装结构并返回
        return  new searchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages()
                ,goodsPage.getContent(),categories,brands,specs);
    }

    /**
     * @param searchRequest
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //给布尔查询添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));

        //添加过滤条件
        //获取用户选择的过滤信息
        Map<String, Object> filter = searchRequest.getFilter();

        for (Map.Entry<String,Object> entry: filter.entrySet()){
            String key = entry.getKey();

            if (StringUtils.equals("品牌",key)){
                key="brandId";
            }else if (StringUtils.equals("分类",key)){
                key="cid3";
            }else{
                key = "specs."+key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件进行聚合规格参数
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询构件器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);

        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, cid, null, true);

        //添加规格参数的聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).
                    field("specs."+param.getName()+".keyword"));
        });
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        //解析聚合结果集
         List<Map<String ,Object>> specs = new ArrayList<>();
         //*解析聚合结果集，key-聚合名称（规格参数名） value-聚合对象
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for ( Map.Entry<String,Aggregation> entry:aggregationMap.entrySet()){
            //初始化一个Map  {k:规格参数名   options: 聚合的规格参数值}
            Map<String, Object> map = new HashMap<>();
            map.put("k",entry.getKey());
            //初始化一个options集合，手机同种的key
            List<String> options = new ArrayList<>();
            //获取聚合值
            StringTerms terms = (StringTerms) entry.getValue();
            //获取桶集合
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }

    /**获得品牌聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandsAggResult(Aggregation aggregation) {
        //想要获取  桶  必须  先转化成这种字段
        LongTerms terms = (LongTerms) aggregation;
        List<Brand> brands = new ArrayList<>();

        terms.getBuckets().forEach(bucket -> {
            //拿到每一个桶的  id，  也就是分类
            //然后通过分布式接口，进行查询
            Brand brand = brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;

    }

    /**获得   聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //想要获取  桶  必须  先转化成这种字段
        LongTerms terms = (LongTerms) aggregation;
        //把桶里的
        List<Map<String, Object>> categories = new ArrayList<>();
        terms.getBuckets().forEach(bucket -> {
            Map<String ,Object> map = new HashMap<>();
            //拿到每一个桶的  id，  也就是分类
            long id = bucket.getKeyAsNumber().longValue();
            List<String> name = this.categoryClient.queryNameByIds(Arrays.asList(id));//处理成List
            //加入Map里
            map.put("id",id);
            map.put("name",name.get(0));

           //把MAP  加入List
            categories.add(map);
        });
        return categories;
    }

    /**保存索引库里面的数据
     * @param id
     * @throws IOException
     */
    public void save(Long id) throws IOException {
        //查找spu
        Spu spu = this.goodsClient.querySkuBySpuId(id);

            Goods goods = this.buildGoods(spu);
           this.goodsRepository.save(goods);

    }

    /**删除索引库里的数据
     * @param id
     */
    public void delete(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
