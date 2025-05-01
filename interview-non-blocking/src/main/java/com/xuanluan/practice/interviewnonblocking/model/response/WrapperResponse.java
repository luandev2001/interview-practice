package com.xuanluan.practice.interviewnonblocking.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WrapperResponse<T> {
    private String message;
    private T data;
}
