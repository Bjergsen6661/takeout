package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.OrderDetail;
import com.example.demo.bean.Orders;
import com.example.demo.bean.ShoppingCart;
import com.example.demo.bean.User;
import com.example.demo.common.DiyException;
import com.example.demo.mapper.OrderDetailMapper;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrdersService;
import com.example.demo.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-09-22:06
 */
@Service
public class OrderDetailServiceImpl
        extends ServiceImpl<OrderDetailMapper, OrderDetail>
        implements OrderDetailService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrdersService ordersService;

    @Override
    public List<OrderDetail> getOrderDetails(HttpSession session) {
        //获取用户id
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();

        List<OrderDetail> orderDetails = null;

        //获取当前用户购物车数据
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id", userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartQueryWrapper);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new DiyException("购物车为空，无法下单...");
        }

        //获取用户的订单
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.eq("user_id", userId);
        List<Orders> orders = ordersService.list(ordersQueryWrapper);

        //向订单明细表插入数据 -> OrderDetail
        orderDetails= orders.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(item.getId());
            return orderDetail;
        }).collect(Collectors.toList());

        orderDetails= shoppingCartList.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());

            return orderDetail;
        }).collect(Collectors.toList());

        return orderDetails;
    }
}
