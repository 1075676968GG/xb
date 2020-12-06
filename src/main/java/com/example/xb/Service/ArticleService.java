package com.example.xb.Service;


import com.example.xb.dao.ArticleDao;
import com.example.xb.entity.Article;
import com.example.xb.entity.PageResult;
import com.example.xb.entity.User;
import com.example.xb.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleMapper articleMapper;

    public void save(Article article) {
        article.setBrowseCount(0L);      //默认浏览量为0
        article.setPublishDate(new Date());        //当前时间发布
        articleDao.save(article);
    }

    public Page<Article> findPage(String title, Integer page) {
        return articleDao.findByTitleLikeOrderByPublishDateDesc(title, PageRequest.of(page - 1, PageResult.PAGE_SIZE));
    }


    /**
     * 根据id查询文章详情
     *
     * @param id
     * @return
     */
    public Article findById(Long id) {
        return articleDao.findById(id).get();
    }

    /**
     * 根据文章id查询收藏数
     *
     * @param articleId
     * @return
     */
    public Integer countFavoriteByArticleId(Long articleId) {
        return articleDao.countFavoriteByArticleId(articleId);
    }

    /**
     * 查询userId用户是否收藏过articleId这篇文章
     *
     * @param userId
     * @param articleId
     * @return
     */
    public Boolean isFavorite(Long userId, Long articleId) {
        return articleDao.countFavoriteByUserIdAndArticleId(userId, articleId) > 0 ? true : false;
    }

    /**
     * 查询我关注的好友列表中也收藏这篇文章的用户
     *
     * @param userId
     * @param articleId
     * @return
     */
    public List<User> findFavoriteListByUserIdAndArticleId(Long userId, Long articleId) {

        Map param = new HashMap<>();
        param.put("userId", userId);
        param.put("articleId", articleId);

        return articleMapper.findFavoriteListByUserIdAndArticleId(param);
    }

    /**
     * 查询userId是否有发布过articleId这篇文章
     *
     * @param userId
     * @param articleId
     * @return
     */
    public Boolean isPublish(Long userId, Long articleId) {
        return articleDao.countByUserIdAndArticleId(userId, articleId) > 0 ? true : false;
    }

    /**
     * 更新浏览量
     *
     * @param id
     */
    @Transactional
    public void updateBrowserCount(Long id) {
        articleDao.updateBrowserCount(id);
    }

    /**
     * 取消收藏
     * @param userId
     * @param articleId
     */
    @Transactional
    public void unFavorite(Long userId, Long articleId) {
        articleDao.deleteFavorite(userId,articleId);
    }

    /**
     * 文章收藏
     * @param userId
     * @param articleId
     */
    @Transactional
    public void favorite(Long userId, Long articleId) {
        articleDao.insertFavorite(userId,articleId);
    }

    public Page<Article> favoritePage(Long userId, String title, Integer page) {
        return articleDao.favoritePage(userId,title,PageRequest.of(page-1,PageResult.PAGE_SIZE));
    }
}