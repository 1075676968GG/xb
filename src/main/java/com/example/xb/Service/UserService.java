package com.example.xb.Service;

import com.example.xb.dao.UserDao;
import com.example.xb.entity.PageResult;
import com.example.xb.entity.User;
import com.example.xb.mapper.UserMapper;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @author liao
 * @date 2020/11/30 19:45
 * @Description
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据邮箱查询用户
     *
     * @param email
     * @return
     */
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * 根据用户名查询用户
     * @Param username
     * @return
     * */
    public User findByUsername(String username) {

        return userDao.findByUsername(username);
    }

    @Transactional
    public void save(User user) {
        user.setIsSecret("0");
        user.setLook(0L);
        user.setRegisterTime(new Date());
        if(StringUtils.isNullOrEmpty(user.getPic())){
            user.setPic("https://www.baidu.com/favicon.ico");
        }
        userDao.save(user);
    }

    @Transactional
    public void updatePassword(String email, String password) {
        userDao.updatePassword(email,password);
    }

    @Transactional
    public void updateLoginTime(Long userId) {

        userDao.updateLoginTime(userId, new Date());
    }


    public User findById(Long userId) {
        return userDao.findById(userId).get();
    }



    public Page<User> findPage(String title, Integer page) {
        return userDao.findByUsernameLike(title, PageRequest.of(page-1, PageResult.PAGE_SIZE));
    }

    public List<Integer>findFocus(Long userId){
        return userDao.findFocusByUserId(userId);
    }

    @Transactional
    public void updateLook(Long userId) {
        userDao.updateLook(userId);

    }

    public Integer countFocusByUserId(Long userId) {
        return userDao.countFocusByUserId(userId);
    }

    public Integer countFansByUserId(Long userId) {
        return userDao.countFansByUserId(userId);
    }

    /**
     * 查询某个用户是否有关注某个用户
     *
     * @param userId
     * @param focusId
     * @return
     */
    public Boolean isFocus(Long userId, Long focusId) {

        return userDao.countByUserIdAndFocusId(userId, focusId) > 0 ? true : false;
    }

    /**
     * 取消关注
     *
     * @param userId
     * @param focusId
     */
    @Transactional
    public void unFocus(Long userId, Long focusId) {
        userDao.deleteFocus(userId, focusId);
    }

    /**
     * 关注
     *
     * @param userId
     * @param focusId
     */
    @Transactional
    public void focus(Long userId, Long focusId) {
        userDao.insertFocus(userId, focusId);
    }

    public void update(User dbUser) {
        userDao.save(dbUser);
    }

    @Transactional
    public void updatePicUrl(Long id, String url) {
        userDao.updatePicUrl(id,url);
    }

    public Page findFocusList(Long id, Integer page) {
        return userDao.findFocusList(id,PageRequest.of(page-1,PageResult.PAGE_SIZE));
    }

    public List<User> findByDeptId(long deptId) {
        return userDao.findByDeptId(deptId);
    }

    public User findByWxOpenid(String openid) {
        return userDao.findByWxOpenid(openid);
    }


}


