package com.example.xb.mapper;

import com.example.xb.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {


    List<User> findFavoriteListByUserIdAndArticleId(Map param);
}
