package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.bean.Employee;

import javax.servlet.http.HttpSession;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-01-14:01
 */

public interface EmployeeService extends IService<Employee> {

    //根据登录传来的信息，进行校验
    int getLoginStatus(Employee employee, HttpSession session);

    //新增保存员工信息
    void defaultSave(Employee employee);

    //分页查询员工信息
    Page<Employee> getEmployeePage(int pn, int pageSize, String name);

}
