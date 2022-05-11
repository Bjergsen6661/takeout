package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.bean.Dish;
import com.example.demo.common.R;
import com.example.demo.dto.DishDto;
import com.example.demo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-15:47
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    DishService dishService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody  DishDto dishDto){
        log.info("新增菜品：{}", dishDto.getName());

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    //分页查询菜品
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam(value = "page", defaultValue = "1") int pn,
                           int pageSize, String name){

        log.info("菜品分页查询：page = {}, pageSize = {}, name = {}", pn, pageSize, name);

        Page<DishDto> page = dishService.getDishDtoPage(pn, pageSize, name);
        return R.success(page);
    }

    //根据id查菜品信息
    @GetMapping("/{id}")
    public R<DishDto> getByid(@PathVariable Long id){
        log.info("根据id查询菜品信息...");

        DishDto dishDto = dishService.getDishDtoById(id);

        if(dishDto != null) return R.success(dishDto);
        else return R.error("未查询到对应菜品...");
    }

    //修改菜品信息
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品信息：{}", dishDto.toString());

        dishService.updateDishDtoById(dishDto);
        return R.success("菜品信息更改成功！");
    }

    //获取指定菜单分类下启售的菜品，使用缓存
    @GetMapping("/list")
    public R<List<DishDto>> getDish(Dish dish){
        log.info("查询菜单分类：{}下的菜品信息...", dish.getCategoryId());

        List<DishDto> dishDtos = dishService.getDishDtoByCategoryId(dish);

        if(dishDtos != null) return R.success(dishDtos);
        else return R.error("未查询到对应菜品...");
    }

    //（批量）删除菜品 —— 只能删除`停售`菜品
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long>ids){
        log.info("删除套餐：{}", ids.toString());

        dishService.removeWithFlavor(ids);
        return R.success("删除套餐成功");
    }

    //（批量）修改菜品售卖状态
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam List<Long>ids){
        log.info("修改菜品售卖状态：{}", ids.toString());

        dishService.updateStatus(status, ids);
        return R.success("修改菜品售卖状态成功");
    }
}
