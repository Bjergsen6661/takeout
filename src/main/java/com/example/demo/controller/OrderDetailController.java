package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.bean.OrderDetail;
import com.example.demo.bean.User;
import com.example.demo.common.R;
import com.example.demo.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-10-11:37
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    //获取用户订单下的订单明细表
    @GetMapping("/list")
    public R<List<OrderDetail>> list(HttpSession session){
        log.info("获取用户的订单明细表...");

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(session);
        return R.success(orderDetails);
    }
}
