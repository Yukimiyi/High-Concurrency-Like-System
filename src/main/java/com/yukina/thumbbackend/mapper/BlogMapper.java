package com.yukina.thumbbackend.mapper;

import com.yukina.thumbbackend.model.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author asus
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2025-10-15 21:46:30
* @Entity com.yukina.thumbbackend.model.entity.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {
    void batchUpdateThumbCount(@Param("countMap") Map<Long, Long> countMap);
}




