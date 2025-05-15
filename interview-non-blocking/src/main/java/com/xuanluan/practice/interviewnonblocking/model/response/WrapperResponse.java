package com.xuanluan.practice.interviewnonblocking.model.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WrapperResponse<T> {
    private String message;
    private T data;
}
