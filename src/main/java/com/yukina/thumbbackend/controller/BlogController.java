package com.yukina.thumbbackend.controller;

import com.yukina.thumbbackend.common.BaseResponse;
import com.yukina.thumbbackend.common.ResultUtils;
import com.yukina.thumbbackend.exception.ErrorCode;
import com.yukina.thumbbackend.exception.ThrowUtils;
import com.yukina.thumbbackend.model.entity.Blog;
import com.yukina.thumbbackend.model.vo.BlogVO;
import com.yukina.thumbbackend.service.BlogService;
import com.yukina.thumbbackend.service.ThumbService;
import com.yukina.thumbbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("blog")
public class BlogController {

    @Resource
    private BlogService blogService;

    @Resource
    private UserService userService;

    @Resource
    private ThumbService thumbService;

    /**
     * 新增
     *
     * @param blog
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody Blog blog) {
        ThrowUtils.throwIf(blog == null || blog.getId() < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(blogService.save(blog));
    }


    @PostMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody Blog blog) {
        ThrowUtils.throwIf(blog == null || blog.getId() < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(blogService.updateById(blog));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(Long blogId) {
        ThrowUtils.throwIf(blogId < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(blogService.removeById(blogId));
    }


    @GetMapping("/get")
    public BaseResponse<BlogVO> get(Long blogId, HttpServletRequest request) {
        ThrowUtils.throwIf(blogId < 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(blogService.getBlogById(blogId, request));
    }

    @GetMapping("/list")
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.list();
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }
}
