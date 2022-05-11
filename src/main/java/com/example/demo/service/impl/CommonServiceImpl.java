package com.example.demo.service.impl;

import com.example.demo.common.R;
import com.example.demo.service.CommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-04-11:28
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Value("${food.imgPath}")
    private String filePath;

    @Override
    public R<String> imgUpload(MultipartFile img) {
        //获取图片的后缀：'.png'、'.jpg'、...
        String originalFilename = img.getOriginalFilename();
        String photoType = originalFilename.substring(originalFilename.lastIndexOf("."));

        //用uuid作为文件名
        String imgName = UUID.randomUUID().toString() + photoType;

        //若filePath不存在，需要创建一个目录
        File dir = new File(filePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        //img是一个临时文件，需要进行文件存储
        try {
            img.transferTo(new File(filePath + imgName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(imgName);
    }

    @Override
    public void imgDownload(String imgName, HttpServletResponse response) {

        FileInputStream fileInputStream = null; //输入流
        ServletOutputStream outputStream = null; //输出流

        try {
            //通过输入流读取文件内容
            fileInputStream = new FileInputStream(new File(filePath + imgName));

            //通过输出流将文件写回浏览器
            outputStream = response.getOutputStream();
            response.setContentType("image/jpeg"); //图片文件

            //写操作
            byte[] buffer = new byte[1024];
            int len; //记录每次读取的字节的个数
            while((len = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4.关闭流资源
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

