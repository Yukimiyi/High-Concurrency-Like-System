package com.yukina.thumbbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yukina.thumbbackend.constant.UserConstant;
import com.yukina.thumbbackend.exception.BusinessException;
import com.yukina.thumbbackend.exception.ErrorCode;
import com.yukina.thumbbackend.model.entity.User;
import com.yukina.thumbbackend.service.UserService;
import com.yukina.thumbbackend.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author asus
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-10-15 21:46:30
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    }
}




