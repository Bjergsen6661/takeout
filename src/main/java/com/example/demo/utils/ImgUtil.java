package com.example.demo.utils;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文件上传
 * @create 2022-05-03-16:18
 */
public class ImgUtil {

    //获取图片的后缀：'.png'、'.jpg'、...
    public static String getPhotoType(MultipartFile headerImg) {
        String originalFilename = headerImg.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String photoType = originalFilename.substring(index);
        return photoType;
    }
}
