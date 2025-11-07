package com.yukina.thumbbackend.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yukina.thumbbackend.constant.RedisLuaScriptConstant;
import com.yukina.thumbbackend.constant.ThumbConstant;
import com.yukina.thumbbackend.exception.BusinessException;
import com.yukina.thumbbackend.exception.ErrorCode;
import com.yukina.thumbbackend.exception.ThrowUtils;
import com.yukina.thumbbackend.mapper.ThumbMapper;
import com.yukina.thumbbackend.model.dto.thumb.DoThumbRequest;
import com.yukina.thumbbackend.model.entity.Thumb;
import com.yukina.thumbbackend.model.entity.User;
import com.yukina.thumbbackend.model.enums.LuaStatusEnum;
import com.yukina.thumbbackend.service.BlogService;
import com.yukina.thumbbackend.service.ThumbService;
import com.yukina.thumbbackend.service.UserService;
import com.yukina.thumbbackend.util.RedisKeyUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

/**
 * @author asus
 * @description 针对表【thumb】的数据库操作Service实现
 * @createDate 2025-10-15 21:46:30
 */
@Service("thumbServiceRedis")
public class ThumbServiceRedisImpl extends ServiceImpl<ThumbMapper, Thumb>
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

        String timeSlice = getTimeSlice();

        String tempThumbKey = RedisKeyUtil.getTempThumbKey(timeSlice);
        String userThumbKey = RedisKeyUtil.getUserThumbKey(userId);

        long result = (Long) redisTemplate.execute(
                RedisLuaScriptConstant.THUMB_SCRIPT,
                Arrays.asList(tempThumbKey, userThumbKey),
                loginUser.getId(),
                blogId
        );

        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户已点赞");
        }

        return LuaStatusEnum.SUCCESS.getValue() == result;
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(doThumbRequest == null || doThumbRequest.getBlogId() == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long blogId = doThumbRequest.getBlogId();
        ThrowUtils.throwIf(blogId == null || blogId < 0, ErrorCode.PARAMS_ERROR);

        String timeSlice = getTimeSlice();

        String tempThumbKey = RedisKeyUtil.getTempThumbKey(timeSlice);
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUser.getId());

        long result = (Long) redisTemplate.execute(
                RedisLuaScriptConstant.UNTHUMB_SCRIPT,
                Arrays.asList(tempThumbKey, userThumbKey),
                loginUser.getId(),
                blogId
        );

        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new RuntimeException("用户未点赞");
        }

        return LuaStatusEnum.SUCCESS.getValue() == result;
    }

    private String getTimeSlice() {
        DateTime nowDate = DateUtil.date();
        // 获取到当前时间前
        return DateUtil.format(nowDate, "HH:mm:") + (DateUtil.second(nowDate) / 10) * 10;
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(ThumbConstant.USER_THUMB_KEY_PREFIX + userId.toString(), blogId.toString());
    }
}




