package com.example.xb.controller;

import com.example.xb.Service.DeptService;
import com.example.xb.Service.UserService;
import com.example.xb.entity.Dept;
import com.example.xb.entity.PageResult;
import com.example.xb.entity.Result;
import com.example.xb.entity.User;


import com.example.xb.util.LoginUserUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Id;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author liao
 * @date 2020/11/30 19:44
 * @Description
 */
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpSession session;


    /**
     * 校验邮箱是否已经注册过
     * true: 邮箱已经注册
     * false:邮箱没有注册
     * * @param email * @return
     */

    @GetMapping("/checkEmail/{email}")
    public Result checkEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            return new Result(true, "该邮箱已经注册");
        }
        return new Result(false, "该邮箱没有注册");
    }

    @GetMapping("/checkUsername/{username}")
    public Result checkUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return new Result(true, "用户名已经注册");
        }
        return new Result(false, "用户名没有注册");
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.save(user);
        return new Result(true, "注册成功");
    }

    @PostMapping("/sendEmail/{email}")
    public Result sendEmail(@PathVariable String email) throws Exception {
        System.out.println("邮箱是: " + email);
        String code = RandomStringUtils.randomNumeric(4);
        redisTemplate.opsForValue().set("user:updatePassword:code:" + email, code);
        System.out.println(code);
        return new Result(true, "发送成功", code);
    }

    @PostMapping("/updatePassword/{code}")
    public Result updatePassword(@PathVariable String code, @RequestBody User user) {
        Object codeInRedis = redisTemplate.opsForValue().get("user:updatePassword:code:") + user.getEmail();
        if (code.equals(codeInRedis)) {
            userService.updatePassword(user.getEmail(), user.getPassword());
            return new Result(true, "修改密码成功");

        }
        return new Result(false, "验证码错误");
    }

    @PostMapping("/login/{code}")
    public Result login(@RequestBody User user, @PathVariable String code) throws Exception {
        //Object safeCode=session.getAttribute("safeCode");
        String username = user.getUsername();
        String password = user.getPassword();

        //session中的验证码
        Object sessionInCode = session.getAttribute("safeCode");

        if (!code.equalsIgnoreCase(String.valueOf(sessionInCode))) {
            return new Result(false, "验证码错误");
        }
        User dbUser = userService.findByUsername(username);
        if (dbUser == null || !dbUser.getPassword().equals(password)) {
            return new Result(false, "用户名或密码错误");
        }// 将登录信息存入redis
        redisTemplate.opsForValue().set("user:loginUser:" + dbUser.getId(), dbUser, 30, TimeUnit.MINUTES); // 更改登录时间
        userService.updateLoginTime(dbUser.getId());
        Map returnMap = new HashMap<>();
        returnMap.put("userId", dbUser.getId()); // 密码置空
        dbUser.setPassword(null);
        returnMap.put("loginUser", dbUser);
        return new Result(true, "登录成功", returnMap);
    }

    @PostMapping("/search/{page}")
    public Result search(@RequestBody Map searchMap, @PathVariable Integer page) throws Exception {

        if (searchMap.get("username") == null) {
            searchMap.put("username", "");
        }

        Page<User> pageData = userService.findPage("%" + searchMap.get("username").toString() + "%", page);

        PageResult<User> pageResult = new PageResult<>(pageData.getTotalPages(), pageData.getContent());

        // 查询当前登录的用户关注过的人
        List<Integer> ids = userService.findFocus(LoginUserUtil.getId());

        Map returnMap = new HashMap();

        returnMap.put("pageResult", pageResult);
        returnMap.put("userFocus", ids);

        return new Result(true, "查询成功", returnMap);
    }

    @GetMapping("/{userId}")
    public Result detail(@PathVariable Long userId) {
        Map returnMap = new HashMap();

        if (LoginUserUtil.getId().longValue()!= userId.longValue()) {
            // 查看数+1
            userService.updateLook(userId);
        }

        User user = userService.findById(userId);
        // 查看关注数
        Integer focusCount = userService.countFocusByUserId(userId);
        // 查看粉丝数
        Integer fansCount = userService.countFansByUserId(userId);

        returnMap.put("fansCount", fansCount);
        returnMap.put("focusCount", focusCount);
        returnMap.put("user", user);

        return new Result(true, "查询成功", returnMap);
    }

    /**
     * 用户关注/取消关注
     *
     * @param focusId
     * @return
     * @throws Exception
     */
    @PutMapping("/focus/{focusId}")
    public Result focus(@PathVariable Long focusId) throws Exception {

        // 查询是否已经关注过
        Boolean isFocus= userService.isFocus(LoginUserUtil.getId(), focusId);

        if (isFocus) {

            // 已经关注过(取消关注)
            userService.unFocus(LoginUserUtil.getId(), focusId);

            return new Result(true, "取关成功");
        }

        // 关注
        userService.focus(LoginUserUtil.getId(), focusId);

        return new Result(true, "关注成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id,@RequestBody User user){
        User dbUser = userService.findById(id);
        Dept dept = deptService.findById(user.getDeptId());
        dbUser.setRealName(user.getRealName());
        dbUser.setDeptName(dept.getName());
        dbUser.setDeptId(user.getDeptId());
        dbUser.setPhone(user.getPhone());
        dbUser.setAge(user.getAge());
        dbUser.setGender(user.getGender());
        dbUser.setIsSecret(user.getIsSecret());
        userService.update(dbUser);
        redisTemplate.opsForValue().set("loginUser:"+dbUser.getId(),dbUser,30,TimeUnit.MINUTES);

        return new Result(true,"修改成功",dbUser);
    }

    @PostMapping("/uploadPic")
    public Result updatePic(MultipartFile pic) throws IOException {
        //文件上传时的名字
        String filename = pic.getOriginalFilename();
        //abcdefg.png(从.开始截取)
        String suffix = filename.substring(filename.lastIndexOf("."));
        //创建新的文件名(防止图片覆盖)
        filename = UUID.randomUUID().toString() + suffix;
        //文件磁盘路径
        String dirPath="D:/upload/" + filename;
        //写出图片
        pic.transferTo(new File(dirPath));
        //文件网络地址
        String url = "http://localhost:8080/upload/" + filename;
        // 更改图片路径
        userService.updatePicUrl(LoginUserUtil.getId(), url);

        User loginUser = LoginUserUtil.getLoginUser();
        loginUser.setPic(url);

        // 更改Redis中的用户信息
        redisTemplate.opsForValue().set("loginUser:" + loginUser.getId(), loginUser, 30, TimeUnit.MINUTES);

        // 把url带到前端(更改localStorage中的url地址)
        return new Result(true, "上传成功", url);
    }

    /**
     * 查询我关注的用户列表+分页
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/findFocusPage/{page}")
    public Result findFocusPage(@PathVariable Integer page) throws Exception {

        Long id = LoginUserUtil.getId();

        Page<User> pageData = userService.findFocusList(id, page);
        return new Result(true, "查询成功",
                new PageResult<User>(pageData.getTotalPages(), pageData.getContent()));
    }

    /**
     * 根据部门id查询用户
     * @param deptId
     * @return
     */
    @GetMapping("/findByDeptId/{deptId}")
    public Result findByDeptId(@PathVariable Long deptId){
        return new Result(true,"查询成功",userService.findByDeptId(deptId));
    }

    @GetMapping("/isSecret/{userId}")
    public Result isSecret(@PathVariable Long userId){
        return new Result(true,"查询成功",userService.findById(userId));
    }

    /**
     * 用户注销
     *
     * @return
     */
    @PostMapping("/logout")
    public Result logout() {

        // 删除redis中的信息
        redisTemplate.delete("loginUser:" + session.getAttribute("userId"));

        return new Result(true, "注销成功");
    }


}




