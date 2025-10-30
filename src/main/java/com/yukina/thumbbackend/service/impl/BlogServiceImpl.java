package com.yukina.thumbbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yukina.thumbbackend.constant.ThumbConstant;
import com.yukina.thumbbackend.exception.ErrorCode;
import com.yukina.thumbbackend.exception.ThrowUtils;
import com.yukina.thumbbackend.model.entity.Blog;
import com.yukina.thumbbackend.model.entity.Thumb;
import com.yukina.thumbbackend.model.entity.User;
import com.yukina.thumbbackend.model.vo.BlogVO;
import com.yukina.thumbbackend.service.BlogService;
import com.yukina.thumbbackend.mapper.BlogMapper;
import com.yukina.thumbbackend.service.ThumbService;
import com.yukina.thumbbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author asus
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2025-10-15 21:46:30
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService{

    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public BlogVO getBlogById(long blogId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Blog blog = this.getById(blogId);
        return getBlogVO(blog, loginUser);
    }

    /**
     * 批量获取点赞
     * @param blogList
     * @param request
     * @return
     */
    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Map<Long, Boolean> ThumbMap = new HashMap<>();
        List<Object> blogIdList= blogList.stream().map(blog -> blog.getId().toString()).collect(Collectors.toList());
//        List<Thumb> thumbList = thumbService.lambdaQuery()
//                .eq(Thumb::getUserId, loginUser.getId())
//                .in(Thumb::getBlogId, blogSet)
//                .list();
        List<Object> thumbList = redisTemplate.opsForHash().multiGet(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogIdList);
        for (int i = 0; i < thumbList.size(); i++) {
            if (thumbList.get(i) == null) {
                continue;
            }
            ThumbMap.put(Long.valueOf(blogIdList.get(i).toString()), true);
        }
        List<BlogVO> blogVOList = blogList.stream().map(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            blogVO.setHasThumb(ThumbMap.get(blog.getId()));
            return blogVO;
        }).toList();
        return blogVOList;
    }

    public BlogVO getBlogVO(Blog blog, User loginUser) {
        BlogVO blogVO = new BlogVO();
        BeanUtils.copyProperties(blog, blogVO);

        if (loginUser == null) {
            return blogVO;
        }

//        Thumb thumb = thumbService.lambdaQuery()
//                .eq(Thumb::getUserId, loginUser.getId())
//                .eq(Thumb::getBlogId, blog.getId())
//                .one();
        Boolean exist = thumbService.hasThumb(blog.getId(), loginUser.getId());

        blogVO.setHasThumb(exist);

        return blogVO;
    }

}




