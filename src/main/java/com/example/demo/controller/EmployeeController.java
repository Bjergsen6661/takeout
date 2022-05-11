package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.bean.Employee;
import com.example.demo.common.R;
import com.example.demo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-01-14:19
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee,
                             HttpSession session){

        //获取登录校验结果
        int loginStatus = employeeService.getLoginStatus(employee, session);

        if(loginStatus == 0 || loginStatus == 1){
            return R.error("登陆失败,账户密码错误");
        }else if(loginStatus == 2){
            return R.error("登陆失败,该账户已被禁用");
        }else{
            Employee curEmployee = (Employee) session.getAttribute("employee");
            return R.success(curEmployee);
        }
    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        //退出当前账号，要清理session
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工，保存员工信息
    @PostMapping
    public R<String> save(@RequestBody Employee employee,
                          HttpSession session){
        log.info("新增员工，员工信息账号：{}", employee.getName());

        //存入数据库，默认密码：123456
        employeeService.defaultSave(employee);
        return R.success("新增员工成功");
    }

    //分页查询员工
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam(value = "page", defaultValue = "1") int pn,
                        int pageSize, String name){
        log.info("员工分页查询：page = {}, pageSize = {}, name = {}", pn, pageSize, name);

        Page<Employee> page = employeeService.getEmployeePage(pn, pageSize, name);
        return R.success(page);
    }


    //根据id修改员工信息
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        log.info("修改员工信息：{}", employee.toString());

        //更新数据库
        employeeService.updateById(employee);

        return R.success("账号状态更改成功！");
    }

    //通过id查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);

        if(employee != null) return R.success(employee);
        else return R.error("未查询到对应员工...");
    }
}
