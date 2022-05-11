package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.Employee;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-01-14:01
 */
@Service
public class EmployeeServiceImpl
        extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {


    /**
     * 登录验证：账户密码 + 禁用状态
     * 0：数据库中不存在该账户
     * 1：输入密码不匹配
     * 2：该账户为禁用状态
     * 3：登录成功
     */
    @Override
    public int getLoginStatus(Employee employee, HttpSession session) {

        //1、通过传入的用户名去数据库中查找用户数据
        QueryWrapper<Employee> qw = new QueryWrapper<>();
        qw.eq("username", employee.getUsername());
        Employee one = super.getOne(qw);

        //2、信息判断
        if(one == null) return 0;
        //获取密码的md5格式，便于在数据库中校验
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        if(!one.getPassword().equals(password)) return 1;
        if(one.getStatus() == 0) return 2;

        //获取成功，记录当前用户，并返回3
        session.setAttribute("employee", one);
        return 3;
    }

    /**
     * 新增员工信息
     * @param employee：表单传来的尸体数据，包含：name,username,phone,sex,id_number,
     */
    @Override
    public void defaultSave(Employee employee) {

        //初始化密码：123456，需要MD5加密处理存储数据库
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        //默认创建其它属性，使用mp的自动填充功能，在MyMetaObjectHandler中处理

        super.save(employee);
    }

    @Override
    public Page<Employee> getEmployeePage(int pn, int pageSize, String name) {
        //构建分页构造器
        Page<Employee> page = new Page<>(pn, pageSize); //查第pn页，一页放pageSize条数据

        //构造查询过滤器
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderByDesc("update_time"); //以更新时间降序排序

        //分页查询结果
        Page<Employee> pageRes = super.page(page, queryWrapper);
        return pageRes;
    }

}
