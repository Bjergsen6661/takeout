package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.bean.Setmeal;
import com.example.demo.common.R;
import com.example.demo.dto.DishDto;
import com.example.demo.dto.SetmealDto;
import com.example.demo.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-05-12:10
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    //新增套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐：{}", setmealDto.getName());

        setmealService.saveWithDishes(setmealDto);
        return R.success("新增套餐成功");
    }

    //分页查询套餐
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam(value = "page", defaultValue = "1") int pn,
                           int pageSize, String name){

        log.info("套餐分页查询：page = {}, pageSize = {}, name = {}", pn, pageSize, name);

        Page<SetmealDto> page = setmealService.getpage(pn, pageSize, name);
        return R.success(page);
    }

    //根据id查套餐信息
    @GetMapping("/{id}")
    public R<SetmealDto> getByid(@PathVariable Long id){
        log.info("根据id查询套餐信息...");

        SetmealDto setmealDto = setmealService.getSetmealDtoById(id);

        if(setmealDto != null) return R.success(setmealDto);
        else return R.error("未查询到对应菜品...");
    }

    //（批量）删除套餐 —— 只能删除`停售`套餐
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long>ids){
        log.info("删除套餐：{}", ids.toString());

        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    //（批量）更新套餐售卖状态
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam List<Long>ids){
        log.info("修改套餐售卖状态：{}", ids.toString());

        setmealService.updateStatus(status, ids);
        return R.success("修改套餐售卖状态成功");
    }

    //修改套餐信息
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐信息：{}", setmealDto.toString());

        setmealService.updateSetmealById(setmealDto);
        return R.success("套餐信息更改成功！");
    }

    //获取指定菜单分类下启售的套餐
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("查询菜单分类：{}下的套餐信息...", setmeal.getCategoryId());

        List<Setmeal> list = setmealService.getSetmealsByCategoryId(setmeal);
        return R.success(list);
    }

}
