package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.bean.AddressBook;
import com.example.demo.bean.User;
import com.example.demo.common.R;
import com.example.demo.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-06-11:16
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    //新增地址信息
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook, HttpSession session){
        log.info("新增地址：{}", addressBook.getDetail());

        //传入数据缺少用户id，即登录时的用户id，利用session
        User user = (User)session.getAttribute("user");
        Long userId = user.getId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("新增成功");
    }

    //获取当前用户存储的全部地址信息
    @GetMapping("/list")
    public R<List<AddressBook>> getAddressBooks(HttpSession session){

        List<AddressBook> addressBooks = addressBookService.getAddressBooks(session);
        if (addressBooks != null) return R.success(addressBooks);
        else return R.error("暂无地址信息");
    }

    //通过地址id查询获取地址信息
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBook(@PathVariable Long id){
        log.info("根据id查询地址信息...");

        AddressBook one = addressBookService.getAddressBook(id);
        if (one != null) return R.success(one);
        else return R.error("没有找到该对象");

    }

    //更新地址信息
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("修改地址信息：{}", addressBook.toString());

        addressBookService.updateById(addressBook);
        return R.success("保存成功");
    }

    //删除地址信息
    @DeleteMapping
    public R<String> remove(@RequestParam(value = "ids") Long id){
        log.info("删除地址信息：{}", id);

        addressBookService.removeById(id);
        return R.success("删除成功");
    }

    //设置当前地址为默认地址(唯一)
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址：{}", addressBook.getId());

        addressBookService.setCurDefault(addressBook);
        return R.success(addressBook);
    }

    //获取当前用户设置的默认地址，用于支付显示
    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpSession session){
        log.info("查询用户默认地址信息");

        AddressBook one = addressBookService.getDefAddress(session);
        if(one != null) return R.success(one);
        else return R.error("未设置默认地址");
    }

}
