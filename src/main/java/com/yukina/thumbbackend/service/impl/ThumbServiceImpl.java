package com.yukina.thumbbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yukina.thumbbackend.constant.ThumbConstant;
import com.yukina.thumbbackend.exception.ErrorCode;
import com.yukina.thumbbackend.exception.ThrowUtils;
import com.yukina.thumbbackend.model.dto.thumb.DoThumbRequest;
import com.yukina.thumbbackend.model.entity.Blog;
import com.yukina.thumbbackend.model.entity.Thumb;
import com.yukina.thumbbackend.model.entity.User;
import com.yukina.thumbbackend.service.BlogService;
import com.yukina.thumbbackend.service.ThumbService;
import com.yukina.thumbbackend.mapper.ThumbMapper;
import com.yukina.thumbbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author asus
 * @description 针对表【thumb】的数据库操作Service实现
 * @createDate 2025-10-15 21:46:30
 */
//@Service
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb>
        implements ThumbService {

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(doThumbRequest == null || doThumbRequest.getBlogId() == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long blogId = doThumbRequest.getBlogId();
        ThrowUtils.throwIf(blogId == null || blogId < 0, ErrorCode.PARAMS_ERROR);

        Long userId = loginUser.getId();

        // 锁完全包裹事务，避免脏读
        synchronized (userId.toString().intern()) {

            // 开启编程式事务
            return transactionTemplate.execute(status -> {
//                boolean exists = this.lambdaQuery()
//                        .eq(Thumb::getUserId, loginUser.getId())
//                        .eq(Thumb::getBlogId, blogId)
//                        .exists();
                boolean exists = this.hasThumb(blogId, userId);
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "不能重复点赞");
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getUserId, userId)
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount + 1")
                        .update();
                Thumb thumb = new Thumb();
                thumb.setUserId(userId);
                thumb.setBlogId(blogId);
                boolean success = update && this.save(thumb);
                if (success) {
                    redisTemplate.opsForHash().put(ThumbConstant.USER_THUMB_KEY_PREFIX + userId.toString(), blogId.toString(), thumb.getId());
                }
                return success;
            });
        }
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(doThumbRequest == null || doThumbRequest.getBlogId() == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long blogId = doThumbRequest.getBlogId();
        ThrowUtils.throwIf(blogId == null || blogId < 0, ErrorCode.PARAMS_ERROR);

        // 锁完全包裹事务，避免脏读
        synchronized (loginUser.getId().toString().intern()) {

            // 开启编程式事务
            return transactionTemplate.execute(status -> {
//                Thumb thumb = this.lambdaQuery()
//                        .eq(Thumb::getUserId, loginUser.getId())
//                        .eq(Thumb::getBlogId, blogId)
//                        .one();
                Object thumbObj = redisTemplate.opsForHash().get(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId.toString());
                ThrowUtils.throwIf(thumbObj == null, ErrorCode.OPERATION_ERROR, "用户未点赞");
                Long thumbId = Long.valueOf(thumbObj.toString());
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getUserId, loginUser.getId())
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();
                boolean success = update && this.removeById(thumbId);
                if (success) {
                    redisTemplate.opsForHash().delete(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId.toString());
                }
                return success;
            });
        }
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(ThumbConstant.USER_THUMB_KEY_PREFIX + userId.toString(), blogId.toString());
    }
}




