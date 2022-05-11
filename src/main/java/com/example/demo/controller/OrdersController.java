package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.bean.Orders;
import com.example.demo.common.R;
import com.example.demo.dto.OrdersDto;
import com.example.demo.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-09-23:06
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService orderService;

    //用户支付功能
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order, HttpSession session){
        log.info("用户下单支付..{}", order);

        orderService.submit(order, session);
        return R.success("下单成功");
    }

    //app端查询分页信息
    @GetMapping("/userPage")
    public R<Page> getPage(@RequestParam(value = "page", defaultValue = "1") int pn,
                           int pageSize, HttpSession session){
        log.info("订单分页查询：page = {}, pageSize = {}", pn, pageSize);

        Page<OrdersDto> page = orderService.getOrderPage(pn, pageSize, session);
        return R.success(page);
    }

    //pc端分页查询
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam(value = "page", defaultValue = "1") int pn,
                           int pageSize, String number,
                           LocalDateTime beginTime, LocalDateTime endTime){
        log.info("订单分页查询：page = {}, pageSize = {}, number = {}, beginTime = {}, endTime = {}", pn, pageSize, number, beginTime, endTime);

        Page<Orders> page = orderService.getOrderPage(pn, pageSize, number, beginTime, endTime);
        return R.success(page);
    }

    //修改派送状态为页面传来的status
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        log.info("修改订单状态为：{}", orders.toString());

        orderService.changeStatus(orders);
        return R.success("修改订单状态成功");
    }

    //再来一单
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders, HttpSession session){
        log.info("再来一单");

        orderService.getAgain(orders, session);
        return R.success("再来一单");
    }

}
