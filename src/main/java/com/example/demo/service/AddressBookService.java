package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.AddressBook;
import com.example.demo.common.R;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-06-11:14
 */
public interface AddressBookService extends IService<AddressBook> {
    //获取当前用户存储的全部地址信息
    List<AddressBook> getAddressBooks(HttpSession session);

    //通过地址id查询获取地址信息
    AddressBook getAddressBook(Long id);

    //设置当前地址为默认地址(唯一)
    void setCurDefault(AddressBook addressBook);

    //获取当前用户设定的默认地址
    AddressBook getDefAddress(HttpSession session);
}
