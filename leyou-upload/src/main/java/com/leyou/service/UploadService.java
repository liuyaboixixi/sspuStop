package com.leyou.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif","image/fax"," \timage/tiff","image/x-icon","image/png");
    //输出log  日志
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        //校验文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            //文件类型不和法，直接返回null
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }
        //校验文件内容
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage==null){
                LOGGER.info("文件内容不合法：{}",originalFilename);
                return  null;
            }


        //保存到服务器
        file.transferTo( new File("D:\\javaStudy\\JAVACODE\\IDEACODE\\leyou\\leyou-upload\\images\\"+originalFilename));
        //返回url，进行回显
        return "http://image.sspu.nat300.top/images/" + originalFilename;
    } catch (IOException e) {
        LOGGER.info("服务器内部错误：{}", originalFilename);
        e.printStackTrace();
    }
        return null;

    }
}
