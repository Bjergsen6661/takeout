package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.ShoppingCart;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-07-13:13
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    //添加当前`菜品\套餐`至购物车，并返回当前记录
    ShoppingCart addCurCart(ShoppingCart shoppingCart, HttpSession session);

    //查询当前用户id下购物车中的数据
    List<ShoppingCart> getList(HttpSession session);

    //清空购物车
    void deleteShoppintChart(HttpSession session);

    //减少一件或删除购物车中指定`菜品\套餐`
    void subCurCart(ShoppingCart shoppingCart, HttpSession session);
}
