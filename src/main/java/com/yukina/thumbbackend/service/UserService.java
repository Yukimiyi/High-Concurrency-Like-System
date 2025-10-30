package com.yukina.thumbbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yukina.thumbbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yukina.thumbbackend.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author asus
* @description 针对表【user】的数据库操作Service
* @createDate 2025-10-15 21:46:30
*/
/**
 * 用户服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserService extends IService<User> {

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


}