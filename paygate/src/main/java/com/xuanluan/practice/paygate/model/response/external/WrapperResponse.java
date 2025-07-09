package com.xuanluan.practice.paygate.model.response.external;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WrapperResponse<T> {
    private String message;
    private T data;
}
