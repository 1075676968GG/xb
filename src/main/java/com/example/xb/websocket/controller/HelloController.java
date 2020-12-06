//package com.example.xb.websocket.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//public class HelloController {
//
//    /**
//     * @author liao
//     * @date 2020/12/5 12:25
//     * @Description
//     */
//
//    @PostMapping(value = "/login")
//    public ModelAndView login(String username) throws Exception{
//        System.out.println(username + "登录啦！");
//
//        ModelAndView view=new ModelAndView();
//        view.addObject("username",username);
//        view.setViewName("chat");
//        return view;
//    }
//
//    @GetMapping(value = "/index.html")
//    public String loginView() throws Exception{
//        return "index";
//    }
//}
