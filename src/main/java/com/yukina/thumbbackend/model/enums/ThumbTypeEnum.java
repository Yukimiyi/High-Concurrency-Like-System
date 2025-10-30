package com.yukina.thumbbackend.model.enums;


import lombok.Getter;

/**
 * 点赞类型
 */
@Getter
public enum ThumbTypeEnum {

    INCR(1),
    DECR(2),
    NON(0),
    ;

    private final int value;

    ThumbTypeEnum(int value) {
        this.value = value;
    }
}
