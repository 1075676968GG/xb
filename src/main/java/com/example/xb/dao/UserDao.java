package com.example.xb.dao;

import com.example.xb.entity.User;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


public interface UserDao extends JpaRepository<User,Long> {

    User findByEmail(String email);

    User findByUsername(String username);

    @Query("update User u set u.password=?2 where u.email=?1")
    @Modifying
    void updatePassword(String email, String password);

    @Query("update User u set u.loginTime=?2 where u.id=?1")
    @Modifying
    void updateLoginTime(Long userId, Date date);

    Page<User> findByUsernameLike(String title, Pageable of);

    @Query("select uf.userFocusId from UserFocus uf where uf.userId=?1")
    List<Integer> findFocusByUserId(Long userId);

    @Query("select count(1) from UserFocus uf where uf.userId=?1")
    Integer countFocusByUserId(Long userId);

    @Query("select count(1) from UserFocus uf where uf.userFocusId=?1")
    Integer countFansByUserId(Long userId);

    @Query("update User u set u.look=u.look+1 where u.id=?1")
    @Modifying
    void updateLook(Long userId);

    @Query("select count(1) from UserFocus uf where uf.userId=?1 and uf.userFocusId=?2")
    Integer countByUserIdAndFocusId(Long userId, Long focusId);

    @Query(value = "delete from UserFocus uf where uf.userId=?1 and uf.userFocusId=?2")
    @Modifying
    void deleteFocus(Long userId, Long focusId);

    @Query(value = "insert into userfocus values(?1,?2)", nativeQuery = true)
    @Modifying
    void insertFocus(Long userId, Long focusId);


    @Query("update User u set u.pic=?2 where u.id=?1")
    @Modifying
    void updatePicUrl(Long id, String url);


    @Query("select u from UserFocus uf left join User u on uf.userFocusId=u.id where uf.userId=?1")
    Page findFocusList(Long id, Pageable of);

    List<User> findByDeptId(long deptId);

    User findByWxOpenid(String openid);
}
