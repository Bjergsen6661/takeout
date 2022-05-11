package com.example.demo.common;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 自定义业务异常类
 * @create 2022-05-03-15:18
 */
public class DiyException extends RuntimeException{

    //属性
    static final long serialVersionUID = -7034897190745766938L;

    //构造器
    public DiyException(){}

    public DiyException(String message){
        super(message);
    }
}
