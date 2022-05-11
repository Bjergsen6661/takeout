package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.Orders;
import com.example.demo.dto.OrdersDto;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-09-23:07
 */
public interface OrdersService extends IService<Orders> {

    //下单支付
    void submit(Orders order, HttpSession session);

    //分页查询
    Page<OrdersDto> getOrderPage(int pn, int pageSize, HttpSession session);

    //分页查询
    Page<Orders> getOrderPage(int pn, int pageSize, String orderId, LocalDateTime beginTime, LocalDateTime endTime);

    //修改派送状态为页面传来的status
    void changeStatus(Orders orders);

    //再来一单
    void getAgain(Orders orders, HttpSession session);
}
