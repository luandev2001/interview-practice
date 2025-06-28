package com.xuanluan.practice.paygate.external;

import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.property.VNPayProperty;
import com.xuanluan.practice.paygate.model.response.external.VNPayResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.xuanluan.practice.paygate.constant.ServiceConstant.*;

@RequiredArgsConstructor
@Component
public class VNPayClient {
    private final RestTemplate restTemplate;
    private final VNPayProperty property;
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(property.getTime().getFormatter());

    public VNPayResponse deposit(Payment payment) {
        MultiValueMap<String, Object> formData = buildDepositDefault();
        formData.add("vnp_TxnRef", payment.getId());
        formData.add("vnp_Amount", payment.getReceivedAmount());
        formData.add("vnp_OrderInfo", payment.getDescription());
        
        ZonedDateTime createDate = payment.getCreatedAt().atZone(ZoneId.of(property.getTime().getZone()));
        ZonedDateTime expireDate = createDate.plusMinutes(property.getTime().getExpireMinute());

        formData.add("vnp_CreateDate", createDate.format(TIME_FORMATTER));
        formData.add("vnp_ExpireDate", expireDate.format(TIME_FORMATTER));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(formData, defaultHeaders());
        return restTemplate.postForObject(property.getUrl(), request, VNPayResponse.class);
    }

    private MultiValueMap<String, Object> buildDepositDefault() {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("vnp_Version", property.getVersion());
        formData.add("vnp_TmnCode", property.getTmnCode());
        formData.add("vnp_SecureHash", property.getHashSecret());
        formData.add("vnp_CurrCode", VNPAY.CURRENCY_CODE);
        formData.add("vnp_Command", property.getCommand());
        formData.add("vnp_Locale", property.getLocale());
        return formData;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return httpHeaders;
    }
}
