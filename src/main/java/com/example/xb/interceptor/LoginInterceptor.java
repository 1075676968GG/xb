package com.example.xb.interceptor;


import com.example.xb.util.LoginUserUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 受限内容免密登录处理
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 请求的URL
        StringBuffer requestURL = request.getRequestURL();

        System.out.println(requestURL);
        if (LoginUserUtil.getLoginUser() == null) {

            //重定向到登录页面
            response.sendRedirect( "/index.html");

            return false;
        }

        return true;
    }
}