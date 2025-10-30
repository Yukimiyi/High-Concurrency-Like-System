package com.yukina.thumbbackend.controller;

import com.yukina.thumbbackend.common.BaseResponse;
import com.yukina.thumbbackend.common.ResultUtils;
import com.yukina.thumbbackend.model.dto.thumb.DoThumbRequest;
import com.yukina.thumbbackend.service.ThumbService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("thumb")
public class ThumbController {
    @Resource
    private ThumbService thumbService;

    @PostMapping("/do")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean result = thumbService.doThumb(doThumbRequest, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/undo")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean result = thumbService.undoThumb(doThumbRequest, request);
        return ResultUtils.success(result);
    }
}
