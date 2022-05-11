package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.User;
import com.example.demo.common.DiyException;
import com.example.demo.common.R;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.utils.SMSUtils;
import com.example.demo.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-05-17:37
 */
@Slf4j
@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper,User>
        implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void sentCode(String phoneNum) {
        //生成随机的6位验证码
        String code = ValidateCodeUtils.generateValidateCode(6).toString();

        //调用第三方API短信服务 —— 前100次免费
        SMSUtils smsUtils = new SMSUtils();
        //您的验证码为：{1}，{2}分钟内有效，如非本人操作，请忽略本短信！
        smsUtils.sendSms(phoneNum, code, "1");

        //将获取到的验证码保存到redis，设置过期时间60s
        redisTemplate.opsForValue().set("code", code, 60, TimeUnit.SECONDS);
    }

    @Override
    public R<User> getUserR(Map map, HttpSession session) {
        //获取用户输入的手机号
        String phone = map.get("phone").toString();
        //获取用户输入的验证码
        String code = map.get("code").toString();

        if("123456".equals(code)){
            //若为新用户，则自动注册进user表中；老用户就无需操作
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            User user = super.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1); //用户状态：正常(1)
                super.save(user);
            }
            //校验成功，即登陆成功（存入当前user到session，用于filter校验）
            session.setAttribute("user", user);
            return R.success(user);
        }else{
            return R.error("验证码错误，请重新输入...");
        }

        /*************************************************************************/

//        //获取redis中保存的验证码
//        String redisCode = (String) redisTemplate.opsForValue().get("code");
//
//        if(redisCode != null && redisCode.equals(code)){
//            //若为新用户，则自动注册进user表中；老用户就无需操作
//            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("phone", phone);
//            User user = super.getOne(queryWrapper);
//            if(user == null){
//                user = new User();
//                user.setPhone(phone);
//                user.setStatus(1); //用户状态：正常(1)
//                super.save(user);
//            }
//            //校验成功，即登陆成功（存入当前user到session，用于filter校验）
//            session.setAttribute("user", user);
//            return R.success(user);
//        }else{
//            //校验失败，即登陆失败
//            return R.error("验证码错误，请重新输入...");
//        }
    }
}
