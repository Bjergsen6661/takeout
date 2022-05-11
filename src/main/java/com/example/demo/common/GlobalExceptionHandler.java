package com.example.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 全局异常处理器
 * @create 2022-05-02-13:15
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //处理`新增/修改相同账号`时报出的异常
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public R<String> MyHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());
        String msg = "未知错误";

        //处理`重复添加`
        if(e.getMessage().contains("Duplicate entry")){
            String[] s = e.getMessage().split(" "); //获取重复的字段
            msg = s[2] + "已存在，无法添加";
        }
        return R.error(msg);
    }

    //处理自定义异常类在抛出的异常
    @ExceptionHandler({DiyException.class})
    public R<String> MyHandler(DiyException e){
        log.error(e.getMessage());

        return R.error(e.getMessage());
    }
}
