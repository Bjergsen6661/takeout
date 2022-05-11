package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.Category;
import com.example.demo.bean.Dish;
import com.example.demo.bean.Setmeal;
import com.example.demo.common.DiyException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.service.CategoryService;
import com.example.demo.service.DishService;
import com.example.demo.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-13:34
 */
@Service
public class CategoryServiceImpl
        extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    @Override
    public Page<Category> getCategoryPage(int pn, int pageSize) {
        //分页构造器
        Page<Category> Page = new Page<Category>(pn, pageSize);

        //查询构造器
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>(); //默认查询所有
        queryWrapper.orderByAsc("sort"); //根据 sort 升序

        Page<Category> pageRes = categoryService.page(Page, queryWrapper);
        return pageRes;
    }

    @Override
    public void ifNotrelevancyDelete(long id) {
        //查询当前菜单分类是否关联菜品
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("category_id", id);
        int dishCount = dishService.count(dishQueryWrapper); //找出有多少个关联菜品
        if(dishCount > 0){
            //抛出一个异常
            throw new DiyException("当前菜单分类下关联了菜品，无法删除");
        }

        //查询当前菜单分类是否关联套餐
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id", id);
        int setmealCounts = setmealService.count(setmealQueryWrapper); //找出有多少个关联套餐
        if(setmealCounts > 0){
            //抛出一个异常
            throw new DiyException("当前菜单分类下关联了套餐，无法删除");
        }

        //若无异常，进行删除
        super.removeById(id);
    }
}
