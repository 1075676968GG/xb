package com.example.xb.util;


import com.example.xb.Service.UserService;
import com.example.xb.entity.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Component
public class LoginUserUtil {
    private static RedisTemplate redisTemplate;
    private static HttpSession session;
    private static HttpServletRequest request;
    private static UserService userService;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        LoginUserUtil.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setSession(HttpSession session) {
        LoginUserUtil.session = session;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        LoginUserUtil.request = request;
    }

    @Autowired
    public void setUserService(UserService userService) {
        LoginUserUtil.userService = userService;
    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public static User getLoginUser() {
        Long userId = null;
        /**
         * 从Cookie里面获取
         */
        Cookie cookie = getCookie("userId");
        if (cookie != null) {
            userId = Long.parseLong(cookie.getValue());

            User user = (User) redisTemplate.opsForValue().get("user:loginUser:" + userId);
            if (user == null) {
                user = userService.findById(user.getId());
                redisTemplate.opsForValue().set("user:loginUser:" + user.getId(), user, 7, TimeUnit.DAYS);
            }
            return user;
        }
        return null;
    }

    public static Cookie getCookie(String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static Long getId() {
        if (getLoginUser() != null) {
            return getLoginUser().getId();
        }
        return null;
    }


}