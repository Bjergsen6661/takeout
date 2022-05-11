package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.Dish;
import com.example.demo.bean.DishFlavor;
import com.example.demo.mapper.DishFlavorMapper;
import com.example.demo.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-04-13:27
 */
@Service
public class DishFlavorServiceImpl
        extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {

}
