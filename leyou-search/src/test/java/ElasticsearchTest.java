import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.PageResult;
import leyou.SspuSearchApplication;
import leyou.search.client.GoodsClient;
import leyou.search.pojo.Goods;
import leyou.search.repository.GoodsRepository;
import leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SspuSearchApplication.class)
public class ElasticsearchTest {

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SearchService searchService;
    @Test
    public void createIndex(){
        //创建索引库，以及映射
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);

        Integer page=21;
        Integer rows=10;

        do {
            //分批查询spubo
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuBoPage(null, null, page, rows);
            //遍历spubo集合转化为List<goods>
            List<Goods> goodsList = pageResult.getItems().stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goodsList);
            // 获取当前页的数据条数，如果是最后一页，没有100条
            rows = pageResult.getItems().size();
            // 每次循环页码加1
            page++;
        }while (rows==10);
    }
}
