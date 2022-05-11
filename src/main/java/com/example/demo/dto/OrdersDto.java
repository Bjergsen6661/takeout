package com.example.demo.dto;

import com.example.demo.bean.OrderDetail;
import com.example.demo.bean.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-10-23:21
 */
@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails = new ArrayList<>();
}
