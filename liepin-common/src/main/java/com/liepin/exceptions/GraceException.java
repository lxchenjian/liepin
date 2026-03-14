package com.liepin.exceptions;

import com.liepin.grace.result.ResponseStatusEnum;

/**
 * 优雅的处理异常，统一进行封装
 * 降低代码侵入性
 */
public class GraceException {

    public static void display(ResponseStatusEnum statusEnum) {
        throw new MyCustomException(statusEnum);
    }

}
