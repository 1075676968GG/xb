package com.example.xb.dao;

import com.example.xb.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface ArticleDao extends JpaRepository<Article,Long> {


    @Query("select count(1) from Favorite f where f.aId=?1")
    Integer countFavoriteByArticleId(Long id);

    @Query("select count(1) from Favorite f where f.uId=?1 and f.aId=?2")
    Integer countFavoriteByUserIdAndArticleId(Long userId, Long articleId);

    @Query("select count(1) from Article a where a.userId=?1 and a.id=?2")
    Integer countByUserIdAndArticleId(Long userId, Long articleId);

    @Query("update Article a set a.browseCount=a.browseCount+1 where a.id=?1")
    @Modifying
    void updateBrowserCount(Long id);

    Page<Article> findByTitleLikeOrderByPublishDateDesc(String title, Pageable of);

    @Query("delete from Favorite f where f.uId=?1 and f.aId=?2")
    @Modifying
    void deleteFavorite(Long userId, Long articleId);

    @Query(value = "insert into favorite values(?1,?2)", nativeQuery = true)
    @Modifying
    void insertFavorite(Long userId, Long articleId);

    @Query("select a from Favorite f left join Article a on f.aId=a.id where f.uId=?1 and a.title like ?2 order by a.publishDate desc ")
    Page<Article> favoritePage(Long userId,String title, Pageable of);

}
