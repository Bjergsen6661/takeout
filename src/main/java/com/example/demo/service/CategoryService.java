package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.Category;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-13:34
 */
public interface CategoryService extends IService<Category> {

    //分页查询菜单信息
    Page<Category> getCategoryPage(int pn, int pageSize);

    //通过传入菜单分类id进行删除
    void ifNotrelevancyDelete(long id);
}
