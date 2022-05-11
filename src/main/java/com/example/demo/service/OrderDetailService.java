package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.OrderDetail;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-09-22:06
 */
public interface OrderDetailService extends IService<OrderDetail> {
    //获取用户的订单明细表
    List<OrderDetail> getOrderDetails(HttpSession session);
}
