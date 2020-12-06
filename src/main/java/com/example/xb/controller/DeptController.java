package com.example.xb.controller;


import com.example.xb.Service.DeptService;
import com.example.xb.Service.UserService;
import com.example.xb.entity.Result;
import com.example.xb.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserService userService;

    /**
     * 查询全部部门信息
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, "查询成功", deptService.findAll());
    }

    @GetMapping("/findDeptAll")
    public Result findDeptAll(){
        List<Map> deptData=deptService.findDeptAll();
        for (Map dept:deptData){
            long deptId = Long.parseLong(dept.get("id").toString());
            List<User> userList=userService.findByDeptId(deptId);
            dept.put("userList",userList);
        }
        return new Result(true,"查询成功",deptData);
    }
}