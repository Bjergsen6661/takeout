package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.ShoppingCart;
import com.example.demo.bean.User;
import com.example.demo.mapper.ShoppingCartMapper;
import com.example.demo.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-07-13:13
 */
@Service
public class ShoppingCartServiceImpl
        extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

    /**
     * @param shoppingCart：页面传来的当前`菜品\套餐`信息
     *  -情况1：若已经添加过购物车 -> 更新数量+1
     *  -情况2：之前未添加 -> 新增保存（数量为1）
     */
    @Override
    public ShoppingCart addCurCart(ShoppingCart shoppingCart, HttpSession session) {
        //获取当前用户的id，需要保存，并且用于queryWrapper
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        shoppingCart.setUserId(userId);

        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null, "user_id", userId);

        //添加的是菜品
        Long dishId = shoppingCart.getDishId();
        if(dishId != null){
            queryWrapper.eq("dish_id", dishId);
        }
        //添加的是套餐
        Long setmealId = shoppingCart.getSetmealId();
        if(setmealId != null){
            queryWrapper.eq("setmeal_id", setmealId);
        }

        //从数据库中找当前`菜品\套餐`的记录
        ShoppingCart one = super.getOne(queryWrapper);
        if(one != null){
            //[更新] 在原来数量基础上+1
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            super.updateById(one);
        }else{
            //[保存] 添加至购物车并且数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            super.save(shoppingCart);
            one = shoppingCart;
        }
        return one;
    }

    @Override
    public List<ShoppingCart> getList(HttpSession session) {
        //获取当前用户id
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();

        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                    .orderByDesc("create_time");

        List<ShoppingCart> list = super.list(queryWrapper);
        return list;
    }

    @Override
    public void deleteShoppintChart(HttpSession session) {
        //获取当前用户id
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();

        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userId);
        super.remove(queryWrapper);
    }

    @Override
    public void subCurCart(ShoppingCart shoppingCart, HttpSession session) {
        //获取当前用户的id，用于queryWrapper
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();

        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null, "user_id", userId);

        //若为菜品
        Long dishId = shoppingCart.getDishId();
        if(dishId != null){
            queryWrapper.eq("dish_id", dishId);
        }
        //若为套餐
        Long setmealId = shoppingCart.getSetmealId();
        if(setmealId != null){
            queryWrapper.eq("setmeal_id", setmealId);
        }

        //获取到当前指定菜品
        ShoppingCart one = super.getOne(queryWrapper);

        //数量>=2，则数量减1；数量<2则从中删除
        Integer number = one.getNumber();
        if(number >= 2){
            one.setNumber(number - 1);
            super.updateById(one);
        }else{
            one = null;
            super.remove(queryWrapper);
        }
    }
}
