package com.yukina.thumbbackend.service;

import com.yukina.thumbbackend.model.dto.thumb.DoThumbRequest;
import com.yukina.thumbbackend.model.entity.Thumb;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author asus
* @description 针对表【thumb】的数据库操作Service
* @createDate 2025-10-15 21:46:30
*/
public interface ThumbService extends IService<Thumb> {
    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    Boolean hasThumb(Long blogId, Long userId);
}
