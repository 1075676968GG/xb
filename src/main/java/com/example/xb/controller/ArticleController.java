package com.example.xb.controller;


import com.example.xb.Service.ArticleService;
import com.example.xb.entity.Article;
import com.example.xb.entity.PageResult;
import com.example.xb.entity.Result;
import com.example.xb.entity.User;
import com.example.xb.util.LoginUserUtil;
//import com.example.xb.websocket.XBWebSocket;
import com.example.xb.websocket.XBWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;

    /**
     * 新增文章
     *
     * @param article
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Article article) {

        article.setUserId(LoginUserUtil.getId());
        article.setPublishRealName(LoginUserUtil.getLoginUser().getRealName());

        articleService.save(article);
        return new Result(true, "查询成功");
    }

    @PostMapping("/search/{page}")
    public Result search(@RequestBody Map searchMap, @PathVariable Integer page)throws Exception{
        if (searchMap.get("title") == null) {
            searchMap.put("title", "");
        }
        Page<Article> pageData = articleService.findPage("%" + searchMap.get("title").toString() + "%", page);

        PageResult<Article> pageResult = new PageResult<>(pageData.getTotalPages(), pageData.getContent());

        Map returnMap = new HashMap();
        returnMap.put("pageResult", pageResult);
        returnMap.put("title", searchMap.get("title"));

        return new Result(true, "查询成功",returnMap);

    }

    @PostMapping("/{id}")
    public Result save(@PathVariable Long id){
        //文章详情
        Article article=articleService.findById(id);
        //收藏数
        Integer favoriteCount = articleService.countFavoriteByArticleId(id);
        // 当前登录的用户是否收藏过这篇文章
        Boolean isFavorite=articleService.isFavorite(LoginUserUtil.getId(),id);

        // 在我关注的好友列表中,也收藏过这篇文章的人
        List<User> favoriteList=articleService.findFavoriteListByUserIdAndArticleId(LoginUserUtil.getId(),id);

        // 先查询这篇文章是否是我自己发布的
        Boolean isPublish=articleService.isPublish(LoginUserUtil.getId(),id);

        if(!isPublish){
            // 不是我自己发布的: 浏览数+1
            articleService.updateBrowserCount(id);
        }

        Map returnMap=new HashMap();
        returnMap.put("article", article);
        returnMap.put("favoriteCount", favoriteCount);
        returnMap.put("isFavorite", isFavorite);
        returnMap.put("favoriteList", favoriteList);

        return new Result(true, "查询成功",returnMap);

    }

    @PostMapping("/favorite/{id}")
    public Result favorite(@PathVariable Long id){
//        // 先查询我是否收藏过这篇文章
//        Boolean isFavorite = articleService.isFavorite(LoginUserUtil.getId(), id);
//        if(isFavorite){
//            //来取消收藏的
//            articleService.unFavorite(LoginUserUtil.getId(),id);
//            return new Result(false,"取消收藏成功");
//        }else {
//            //来收藏的
//            articleService.favorite(LoginUserUtil.getId(),id);
//            return new Result(true,"收藏成功");
//        }

        // 先查询我是否收藏过这篇文章
        Boolean isFavorite = articleService.isFavorite(LoginUserUtil.getId(), id);

        if (isFavorite) {

            // 来取消收藏的
            articleService.unFavorite(LoginUserUtil.getId(), id);

            Integer favoriteCount = articleService.countFavoriteByArticleId(id);
            return new Result(false, "取消收藏成功", favoriteCount);
        } else {

            // 来收藏的
            articleService.favorite(LoginUserUtil.getId(), id);

            Integer favoriteCount = articleService.countFavoriteByArticleId(id);

            // 查询这篇文章是谁发布的
            Long userId = articleService.findById(id).getUserId();

            // 给这个人推送信息
            XBWebSocket.sendMessage(userId,LoginUserUtil.getLoginUser().getRealName()+"刚刚收藏了您的文章！");

            return new Result(true, "收藏成功", favoriteCount);
        }


    }


    @PostMapping("/favoritePage/{page}")
    public Result favoritePage(@RequestBody Map searchMap, @PathVariable Integer page)  {

        if (searchMap.get("title") == null) {
            searchMap.put("title", "");
        }

        Page<Article> pageData = articleService.favoritePage(
                LoginUserUtil.getId(),
                "%" + searchMap.get("title").toString() + "%", page);

        return new Result(true, "查询成功",
                new PageResult<>(pageData.getTotalPages(), pageData.getContent()));
    }

}