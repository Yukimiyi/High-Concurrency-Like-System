package com.yukina.thumbbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yukina.thumbbackend.common.BaseResponse;
import com.yukina.thumbbackend.common.ResultUtils;
import com.yukina.thumbbackend.constant.UserConstant;
import com.yukina.thumbbackend.exception.ErrorCode;
import com.yukina.thumbbackend.exception.ThrowUtils;
import com.yukina.thumbbackend.model.entity.User;
import com.yukina.thumbbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/add")
    private BaseResponse<Boolean> add(@RequestBody User user) {
        ThrowUtils.throwIf(user == null || user.getId() < 0 || user.getUsername() == null, ErrorCode.PARAMS_ERROR);
        boolean save = userService.save(user);
        return ResultUtils.success(save);
    }

    @PostMapping("/update")
    private BaseResponse<Boolean> update(@RequestBody User user) {
        ThrowUtils.throwIf(user == null || user.getId() < 0 || user.getUsername() == null, ErrorCode.PARAMS_ERROR);
        boolean update = userService.updateById(user);
        return ResultUtils.success(update);
    }

    @PostMapping("/delete")
    private BaseResponse<Boolean> delete(long userId) {
        ThrowUtils.throwIf(userId < 0, ErrorCode.PARAMS_ERROR);
        boolean update = userService.removeById(userId);
        return ResultUtils.success(update);
    }

    @GetMapping("/get")
    private BaseResponse<User> get(long userId) {
        ThrowUtils.throwIf(userId < 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(userId);
        return ResultUtils.success(user);
    }

    @GetMapping("/list")
    private BaseResponse<List<User>> getList() {
        List<User> userList = userService.list();
        return ResultUtils.success(userList);
    }

    @GetMapping("/login")
    private BaseResponse<User> login(long userId, HttpServletRequest request) {
        ThrowUtils.throwIf(userId < 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(userId);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return ResultUtils.success(user);
    }

    @GetMapping("/get/login")
    private BaseResponse<User> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR);
        return ResultUtils.success(user);
    }
}
