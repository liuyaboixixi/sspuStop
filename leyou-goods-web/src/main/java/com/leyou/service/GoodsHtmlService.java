package com.leyou.service;

import com.leyou.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {
    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId)  {
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));

        //生成静态化页面

        PrintWriter printWriter = null;
        try {
            File file = new File("E:\\nginx-1.14.0\\html\\item\\"+spuId+".html");
            printWriter = new PrintWriter(file);
            this.engine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            printWriter.close();
        }


    }

    public void asyncExcute(Long spuId){
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });
    }

    public void deleteHtml(Long id) {
        //首先指定某个文件
        File file = new File("E:\\nginx-1.14.0\\html\\item\\" + id + ".html");
        //此方法代表这个文件存在就删掉
        file.deleteOnExit();
    }
}
