package com.sspu.search.Controller;

import com.sspu.search.pojo.SearchRequest;
import com.sspu.search.pojo.searchResult;
import com.sspu.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Value("${server.port}")
    private String serverPort;

    /**
     * 前端基本查询搜索 实现基本搜索
     *
     * @param searchRequest 把前台传过来的json来转化成对象
     * @return
     */
    @PostMapping("page")
    //@RequestBody SearchRequest searchRequest   把前台传过来的json来转化成对象
    public ResponseEntity<searchResult> search(@RequestBody SearchRequest searchRequest) {
        //从elasticsearch查询
        searchResult result = this.searchService.search(searchRequest);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("集群,负载均衡----------------------调用搜索微服务是:"+serverPort);
        log.info("集群,负载均衡----------------------调用搜索微服务是:"+serverPort);
        return ResponseEntity.ok(result);
    }
}
