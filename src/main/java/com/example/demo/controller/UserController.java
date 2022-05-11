package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.demo.bean.User;
import com.example.demo.common.R;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-05-18:08
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //发送验证码请求
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        log.info("发送验证码...");

        String phoneNum = user.getPhone();
        if(StringUtils.isNotBlank(phoneNum)){
            //进行短信发送
            userService.sentCode(phoneNum);
            return R.success("短信发送成功");
        }
        return R.error("短信发送失败");
    }


    //用户登录验证请求
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //登录验证，并获取当前对象
        return userService.getUserR(map,session);
    }
}
