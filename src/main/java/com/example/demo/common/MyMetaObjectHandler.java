package com.example.demo.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.demo.bean.Employee;
import com.example.demo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 元数据处理，处理自动填写
 *  -通过Threadlocal获取createUser、updateUser
 *  -直接使用session
 * @create 2022-05-03-12:20
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    HttpSession session;

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");

        Employee employee = (Employee) session.getAttribute("employee");
        if(employee != null){
            Long id = employee.getId();

            metaObject.setValue("createTime", LocalDateTime.now());
            metaObject.setValue("updateTime", LocalDateTime.now());
            metaObject.setValue("createUser", id);
            metaObject.setValue("updateUser", id);
        }

        User user = (User) session.getAttribute("user");
        if(user != null){
            Long id = user.getId();

            metaObject.setValue("createTime", LocalDateTime.now());
            metaObject.setValue("updateTime", LocalDateTime.now());
            metaObject.setValue("createUser", id);
            metaObject.setValue("updateUser", id);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");

        Employee employee = (Employee) session.getAttribute("employee");
        if(employee != null){
            Long id = employee.getId();

            metaObject.setValue("updateTime", LocalDateTime.now());
            metaObject.setValue("updateUser", id);
        }

        User user = (User) session.getAttribute("user");
        if(user != null){
            Long id = user.getId();

            metaObject.setValue("updateTime", LocalDateTime.now());
            metaObject.setValue("updateUser", id);
        }
    }
}
