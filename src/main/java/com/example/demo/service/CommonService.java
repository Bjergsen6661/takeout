package com.example.demo.service;

import com.example.demo.common.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-04-11:28
 */
public interface CommonService {

    //文件上传
    R<String> imgUpload(MultipartFile img);

    //文件加载
    void imgDownload(String imgName, HttpServletResponse response);
}
