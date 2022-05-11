package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.bean.Category;
import com.example.demo.common.R;
import com.example.demo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-13:33
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    //新增菜单分类，保存菜单信息
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增菜单：{}", category.getName());
        //默认字段值由mp自动填写，进行保存
        categoryService.save(category);

        return R.success("新增菜单成功");
    }

    //分页查询菜单分类
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam(value = "page", defaultValue = "1") int pn,
                           int pageSize){
        log.info("菜单分页查询：page = {}, pageSize = {}", pn, pageSize);

        Page<Category> page = categoryService.getCategoryPage(pn, pageSize);

        return R.success(page);
    }

    //删除菜单分类信息 —— 菜单分类下有菜品不可删除
    @DeleteMapping()
    public R<String> delete(@RequestParam(value = "ids") long id){
        log.info("删除id：{}的菜单信息", id);

        //无关联则删除
        categoryService.ifNotrelevancyDelete(id);
        return R.success("删除成功");
    }

    //更新菜单分类信息
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改菜单信息：{}", category.toString());

        categoryService.updateById(category);
        return R.success("菜单信息更新成功！");
    }


    //根据菜单分类(type)，查询所属分类的数据
    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category){
        log.info("查询菜单信息...");

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(category.getType() != null, "type", category.getType());
        queryWrapper.orderByAsc("sort").orderByDesc("update_time");

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
