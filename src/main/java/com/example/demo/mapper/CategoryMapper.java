package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.bean.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-13:33
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
