package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.bean.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-06-11:14
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
