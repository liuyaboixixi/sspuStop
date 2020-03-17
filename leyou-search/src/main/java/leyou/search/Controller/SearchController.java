package leyou.search.Controller;

import leyou.search.pojo.SearchRequest;
import leyou.search.pojo.searchResult;
import leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**  前端基本查询搜索 实现基本搜索
     * @param searchRequest 把前台传过来的json来转化成对象
     * @return
     */
    @PostMapping("page")
    //@RequestBody SearchRequest searchRequest   把前台传过来的json来转化成对象
    public ResponseEntity<searchResult>search(@RequestBody SearchRequest searchRequest){
        //从elasticsearch查询
        searchResult result = this.searchService.search(searchRequest);
        if (result == null){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
}
