package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.AddressBook;
import com.example.demo.bean.User;
import com.example.demo.common.R;
import com.example.demo.mapper.AddressBookMapper;
import com.example.demo.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-06-11:15
 */
@Service
public class AddressBookServiceImpl
        extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {

    @Override
    public List<AddressBook> getAddressBooks(HttpSession session) {
        //获取当前用户的id
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        //通过id查询关联的地址信息
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<AddressBook> addressBooks = super.list(queryWrapper);
        return addressBooks;
    }

    @Override
    public AddressBook getAddressBook(Long id) {
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        AddressBook one = super.getOne(queryWrapper);
        return one;
    }

    @Override
    public void setCurDefault(AddressBook addressBook) {
        //若之前有设置默认地址，则需要把它取消
        QueryWrapper<AddressBook> defaultqueryWrapper = new QueryWrapper<>();
        defaultqueryWrapper.eq("is_default", 1);
        AddressBook preDefault = super.getOne(defaultqueryWrapper);
        if(preDefault != null){
            preDefault.setIsDefault(0);
            super.updateById(preDefault);
        }

        //此时再把当前地址设为默认地址
        addressBook.setIsDefault(1);
        super.updateById(addressBook);
    }

    @Override
    public AddressBook getDefAddress(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_default", 1);

        AddressBook one = super.getOne(queryWrapper);
        return one;
    }

}
