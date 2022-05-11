package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.bean.ShoppingCart;
import com.example.demo.bean.User;
import com.example.demo.common.R;
import com.example.demo.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-07-13:12
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    //添加当前`菜品\套餐`至购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart,
                               HttpSession session){
        log.info("添加`{}`至购物车", shoppingCart.getName());

        ShoppingCart one = shoppingCartService.addCurCart(shoppingCart, session);
        return R.success(one);
    }

    //查询当前用户id下购物车中的数据
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session){
        log.info("查询购物车中的数据...");

        List<ShoppingCart> list = shoppingCartService.getList(session);
        return R.success(list);
    }

    //清空购物车
    @DeleteMapping("clean")
    public R<String> delete(HttpSession session){
        log.info("清空购物车中的数据...");

        shoppingCartService.deleteShoppintChart(session);
        return R.success("清空购物车成功");
    }

    //减少一件或删除购物车中指定`菜品\套餐`
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart,
                               HttpSession session){
        log.info("退掉购物车中的`{}`", shoppingCart.getDishId() == null ? shoppingCart.getSetmealId() : shoppingCart.getDishId());

        shoppingCartService.subCurCart(shoppingCart, session);

        return R.success("删除成功");
    }

}
