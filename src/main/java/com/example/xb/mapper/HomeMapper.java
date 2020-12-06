package com.example.xb.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author liao
 * @date 2020/12/4 15:02
 * @Description
 */
@Mapper
public interface HomeMapper {
    Map<String, Long> findHomeCount();

    List<Map<String, Long>> findHomeDetail();

}
