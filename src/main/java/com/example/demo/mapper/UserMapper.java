package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.bean.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-05-17:36
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
