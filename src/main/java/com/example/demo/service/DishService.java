package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.Dish;
import com.example.demo.common.R;
import com.example.demo.dto.DishDto;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-14:53
 */
public interface DishService extends IService<Dish> {

    //保存菜品信息，并保存菜品口味信息
    void saveWithFlavor(DishDto dishDto);

    //分页查询菜品信息
    Page<DishDto> getDishDtoPage(int pn, int pageSize, String name);

    //通过id查询菜品信息
    DishDto getDishDtoById(Long id);

    //修改菜品信息
    void updateDishDtoById(DishDto dishDto);

    //获取指定菜单分类下启售的菜品
    List<DishDto> getDishDtoByCategoryId(Dish dish);

    //（批量）删除菜品 —— 只能删除`停售`菜品
    void removeWithFlavor(List<Long> ids);

    //（批量）修改菜品售卖状态
    void updateStatus(int status, List<Long> ids);
}
