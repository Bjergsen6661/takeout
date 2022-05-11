package com.example.demo.filter;

import com.alibaba.fastjson.JSON;
import com.example.demo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 登录过滤器 未登录只能访问登录页
 * @create 2022-05-01-15:11
 */
@Slf4j
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter { //主程序类上需要加 @ServletComponentScan

    //路径匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //获取请求
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、定义可放行的路径url，包括静态资源
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login", //移动端登录
                "/user/sendMsg" //短信发送请求
        };

        //2、获取本次请求的uri
        String requestURI = request.getRequestURI();

        //3、处理放行情况
        //3.1、判断当前uri是否可放行
        boolean check = checkUri(urls, requestURI);
        if(check){
            filterChain.doFilter(request, response);
            return;
        }

        //3.2.1、判断后台系统登录状态，已登录则放行，未登录则拦截
        if(request.getSession().getAttribute("employee") != null){
            filterChain.doFilter(request, response);
            return;
        }

        //3.2.2、判断用户系统登录状态，已登录则放行，未登录则拦截
        if(request.getSession().getAttribute("user") != null){
            filterChain.doFilter(request, response);
            return;
        }

        //4.处理不可放行请求
        log.info("用户未登录，进行拦截");
        log.info("拦截到请求：{}",requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    //判断当前uri是否可放行
    public boolean checkUri(String[] urls, String curUri){
        for(String url : urls){
            boolean match = PATH_MATCHER.match(url, curUri);
            if(match){
                return true;
            }
        }
        return false;
    }
}
