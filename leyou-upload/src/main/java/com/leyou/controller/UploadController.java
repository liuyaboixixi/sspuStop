package com.leyou.controller;

import com.leyou.service.UploadService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("upload")
public class UploadController {


    @Autowired
    private UploadService uploadService;

    @RequestMapping(value = "image", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file){
       String url = uploadService.uploadImage(file);
        if (StringUtils.isBlank(url)){
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

}
