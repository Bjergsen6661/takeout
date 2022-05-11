package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.bean.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-14:52
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
