package com.xuanluan.practice.paygate.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DepositRequest extends TransferRequest {
    private Map<String, Object> metadata = new HashMap<>();
}
