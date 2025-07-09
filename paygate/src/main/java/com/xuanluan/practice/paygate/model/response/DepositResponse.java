package com.xuanluan.practice.paygate.model.response;

import java.util.UUID;

public record DepositResponse(UUID paymentId, String url) {
}
