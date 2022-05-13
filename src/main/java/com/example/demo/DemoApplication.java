package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan //Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解⾃动注册
@EnableTransactionManagement //开启事务功能
@EnableCaching //开启SpringCache注解缓存
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        log.info("项目启动成功...");
    }

}
