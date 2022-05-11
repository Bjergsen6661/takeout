package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.User;
import com.example.demo.common.R;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-05-17:36
 */
public interface UserService extends IService<User> {
    //为手机号发送验证码
    void sentCode(String phoneNum);

    //用户登录验证
    R<User> getUserR(Map map, HttpSession session);
}
