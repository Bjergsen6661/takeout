package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.Setmeal;
import com.example.demo.dto.SetmealDto;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-14:50
 */
public interface SetmealService extends IService<Setmeal> {

    //新增套餐
    void saveWithDishes(SetmealDto setmealDto);

    //分页查询套餐信息
    Page<SetmealDto> getpage(int pn, int pageSize, String name);

    //(批量)删除套餐，关联套餐下的菜品
    void removeWithDish(List<Long> ids);

    //（批量）更新套餐售卖状态
    void updateStatus(int status, List<Long> ids);

    //根据id查套餐信息
    SetmealDto getSetmealDtoById(Long id);

    //修改套餐信息
    void updateSetmealById(SetmealDto setmealDto);

    //获取指定菜单分类下启售的套餐
    List<Setmeal> getSetmealsByCategoryId(Setmeal setmeal);
}
