package com.leyou.Controller;

import com.leyou.service.GoodsHtmlService;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GoodsController {
@Autowired
private GoodsService goodsService;
@Autowired
private GoodsHtmlService goodsHtmlService;
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model){
       //加载所需要的数据
        Map<String, Object> modelMap = goodsService.loadData(id);
       //放入模型
        model.addAllAttributes(modelMap);

        //生成固定的静态页面   想要生效 需要在negix 服务器中配置
        goodsHtmlService.asyncExcute(id);
        return "item";
    }
}

