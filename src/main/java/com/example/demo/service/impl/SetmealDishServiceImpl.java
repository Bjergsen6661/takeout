package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.SetmealDish;
import com.example.demo.mapper.SetmealDishMapper;
import com.example.demo.service.SetmealDishService;
import org.springframework.stereotype.Service;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-05-12:09
 */
@Service
public class SetmealDishServiceImpl
        extends ServiceImpl<SetmealDishMapper, SetmealDish>
        implements SetmealDishService {
}
