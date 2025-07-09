package com.xuanluan.practice.paygate.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(value = "external.vnpay")
public class VNPayProperty {
    private String url;
    private String returnUrl;
    private String hashSecret;
    private String tmnCode;
    private String version = "2.1.1";
    private String locale = "vn";
    private String command;
    private String currencyCode = "VND";
    private String orderType = "other";
    private Time time = new Time();

    @Getter
    @Setter
    public static class Time {
        private String formatter;
        private String zone;
        private int expireMinute = 15;
    }
}
