package com.example.demo.service.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.*;
import com.example.demo.common.DiyException;
import com.example.demo.dto.DishDto;
import com.example.demo.dto.OrdersDto;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-09-23:07
 */
@Service
public class OrdersServiceImpl
        extends ServiceImpl<OrderMapper, Orders>
        implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders order, HttpSession session) {
        //获取用户id
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();

        //获取当前用户购物车数据
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new DiyException("购物车为空，无法下单...");
        }

        //获取用户信息
        User userInfo = userService.getById(userId);

        //获取地址信息
        Long addressBookId = order.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new DiyException("用户地址信息有误，不能下单");
        }

        //数据准备
        long orderId = IdWorker.getId(); //生成id -> 主键、订单号

        AtomicInteger amount = new AtomicInteger(0); //总金额

        //向订单明细表插入数据 -> OrderDetail
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue()); //累加
            return orderDetail;
        }).collect(Collectors.toList());

        //向订单表插入数据 -> Order
//        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setStatus(2); //状态2：待派送
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setAmount(new BigDecimal(amount.get()));//总金额
        order.setPhone(addressBook.getPhone());
        order.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        order.setUserName(userInfo.getName());
        order.setConsignee(addressBook.getConsignee());
        this.save(order);

        //向订单明细表插入数据 -> OrderDetail
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public Page<OrdersDto> getOrderPage(int pn, int pageSize, HttpSession session) {
        //分页构造器
        Page<Orders> orderPage = new Page<>(pn, pageSize);

        //查询构造器
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("order_time");

        super.page(orderPage, queryWrapper); //保存订单基本信息，尚未保存明细

        //还需要查询`orderDetails`，即订单明细表信息
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(orderPage, ordersDtoPage, "records"); //对象复制

        //获取页面数据records
        List<Orders> orderRecords = orderPage.getRecords();

        //处理records：添加上订单明细
        List<OrdersDto> orderDtoRecords = orderRecords.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);

            String number = item.getNumber(); //获取订单id
            QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
            orderDetailQueryWrapper.eq("order_id", number);
            List<OrderDetail> list = orderDetailService.list(orderDetailQueryWrapper); //获取该订单id下的所有明细
            if(list != null || list.size() != 0){
                ordersDto.setOrderDetails(list);
            }

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(orderDtoRecords);
        return ordersDtoPage;
    }

    @Override
    public Page<Orders> getOrderPage(int pn, int pageSize, String number, LocalDateTime beginTime, LocalDateTime endTime) {
        //构建分页构造器
        Page<Orders> ordersPage = new Page<>(pn, pageSize);

        //构造查询条件
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(number), "number", number)
                    .between(beginTime != null && endTime != null, "checkout_time", beginTime, endTime)
                    .orderByDesc("checkout_time");

        //分页查询结果
        Page<Orders> page = super.page(ordersPage, queryWrapper);
        return page;
    }

    @Override
    public void changeStatus(Orders orders) {
        //构造查询条件，查询指定id的订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orders.getId());
        Orders one = super.getOne(queryWrapper);

        one.setStatus(orders.getStatus());
        super.updateById(one);
    }

    @Override
    public void getAgain(Orders orders, HttpSession session) {
        //获取订单number
        Long id = orders.getId();
        QueryWrapper<Orders> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("id", id);
        Orders one = super.getOne(orderQueryWrapper);
        String number = one.getNumber();

        //获取该订单下的明细表
        QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
        orderDetailQueryWrapper.eq("order_id", number);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailQueryWrapper);

        //数据准备
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();

        //为该用户的购物车附上明细表中所购买的物品
        List<ShoppingCart> shoppingCarts = orderDetails.stream().map((item) ->{
            ShoppingCart shoppingCart = new ShoppingCart();

            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(userId);
            if(item.getDishId() != null) shoppingCart.setDishId(item.getDishId());
            if(item.getSetmealId() != null) shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        //保存该购物车信息
        shoppingCartService.saveBatch(shoppingCarts);
    }
}
