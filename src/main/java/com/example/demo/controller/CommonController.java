package com.example.demo.controller;

import com.example.demo.common.R;
import com.example.demo.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 处理 /common/** 路径下的请求
 * @create 2022-05-03-16:03
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    CommonService commonService;

    @Value("${food.imgPath}")
    private String filePath;

    //文件上传
    @PostMapping("/upload")
    public R<String> upload(@RequestPart("file") MultipartFile img){
        log.info("上传图片：{},大小：{}", img.getName(), img.getSize());

        if(!img.isEmpty()){
            return commonService.imgUpload(img);
        }else {
            return R.error("请正确上传图片");
        }
    }


    //文件加载
    @GetMapping("/download")
    public void download(@RequestParam(value = "name") String imgName,
                         HttpServletResponse response){

        commonService.imgDownload(imgName, response);
    }



}
