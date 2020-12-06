package com.example.xb.controller;

import com.example.xb.Service.UserService;
import com.example.xb.entity.Result;
import com.example.xb.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;


@RestController
@RequestMapping
public class WxLoginController {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String appSecret;

    @Value("${wx.redirect_uri}")
    private String redirect_uri;


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/to_wxLogin")
    public void to_wxLogin() throws Exception {

        //Step1：获取Authorization Code
        String url = "https://open.weixin.qq.com/connect/qrconnect?response_type=code" +
                "&appid=" + appId +
                "&redirect_uri=" + URLEncoder.encode(redirect_uri) +
                "&scope=snsapi_login";

        // 重定向到微信登录指定的地址进行微信登录授权,授权成功后返回code
        response.sendRedirect(url);
    }

    /**
     * 用户确认授权之后的操作
     * @throws IOException
     */
    @RequestMapping("/wx_login")
    public void wx_login() throws IOException {
        //用户扫码成功后携带过来的code
        String code=request.getParameter("code");

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId +
                "&secret=" + appSecret +
                "&code=" + code +
                "&grant_type=authorization_code";

        // 获取AccessToken、openid等数据
        HashMap info = execute(url);

        System.out.println("info: " + info);
        url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + info.get("access_token") +
                "&openid=" + info.get("openid");
        //获取用户信息
        HashMap userInfo=execute(url);
        System.out.println("userInfo"+userInfo);

        //根据微信的openid查询此用户原来没有使用过微信登录
        User user=userService.findByWxOpenid(info.get("openid").toString());

        //说明该用户是第一次使用微信登录
        if(user==null){
            user=new User();
            //用户的头像
            user.setPic(userInfo.get("headimgurl").toString());
            //性别
            user.setGender(userInfo.get("sex").toString());
            // 用户的昵称
            user.setRealName(userInfo.get("nickname").toString());

            // 随机用户名(11位随机字符串)
            user.setUsername(UUID.randomUUID().toString().substring(36 - 15));

            // 注册时间为当前时间
            user.setRegisterTime(new Date());

            user.setWxOpenid(info.get("openid").toString());

            // 密码默认admin
            user.setPassword("admin");
            // 注册一个新的用户
            userService.save(user);

            user = userService.findByWxOpenid(info.get("openid").toString());
        }
        // 修改登录时间
        userService.updateLoginTime(user.getId());
        redisTemplate.opsForValue().set("loginUser:" + user.getId(), user, 7, TimeUnit.DAYS);

        // 存储在redis做中转操作
        redisTemplate.opsForValue().set("userId", user.getId(), 60, TimeUnit.SECONDS);
        response.sendRedirect("/html/wx_login_info.html");
    }
    /**
     * 执行URL请求
     * @param url
     * @return
     */
    private HashMap execute(String url) {

        try {
            // 创建一个http Client请求
            CloseableHttpClient client = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();       // 获取响应数据(json)

            if (entity != null) {
                String result = EntityUtils.toString(entity, Charset.forName("UTF8"));

                return new ObjectMapper().readValue(result, HashMap.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/getWxLoginInfo")
    public Result getWxLoginInfo()throws IOException{
        Object userId = session.getAttribute("userId");
        Map returnMap=new HashMap<>();

        returnMap.put("loginUser",redisTemplate.opsForValue().get("loginUser:"+userId));
        returnMap.put("userId",userId);
        return new Result(true,"微信登录成功",returnMap);
    }



}