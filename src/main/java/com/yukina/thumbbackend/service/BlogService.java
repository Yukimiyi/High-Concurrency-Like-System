package com.yukina.thumbbackend.service;

import com.yukina.thumbbackend.model.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yukina.thumbbackend.model.vo.BlogVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author asus
* @description 针对表【blog】的数据库操作Service
* @createDate 2025-10-15 21:46:30
*/
public interface BlogService extends IService<Blog> {

    BlogVO getBlogById(long blogId, HttpServletRequest request);

    List<BlogVO> getBlogVOList(List<Blog> blogList,HttpServletRequest request);
}
