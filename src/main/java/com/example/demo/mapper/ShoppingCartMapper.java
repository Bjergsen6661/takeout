package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.bean.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-07-13:12
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
