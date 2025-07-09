package com.xuanluan.practice.paygate.external;

import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.exception.BadRequestException;
import com.xuanluan.practice.paygate.model.property.VNPayProperty;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Component
public class VNPayClient {
    private final VNPayProperty property;

    /**
     * * Link tài liệu: <a href="https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html">link</a>
     * <p>
     * * Thông tin bắt buộc:
     * vnp_Version, vnp_Command, vnp_TmnCode, vnp_Amount, vnp_CreateDate, vnp_CurrCode, vnp_Locale, vnp_IpAddr,
     * vnp_OrderInfo, vnp_OrderType, vnp_ReturnUrl, vnp_TxnRef, vnp_SecureHash
     * <p>
     * * Thông tin tuỳ chọn:
     * vnp_BankCode (VNPAYQR, VNBANK, INTCARD)
     */
    public String createUrl(Payment payment, Map<String, Object> metadata) {
        Map<String, String> params = buildDepositDefault();
        params.put("vnp_TxnRef", payment.getId().toString());
        params.put("vnp_Amount", String.valueOf(payment.getReceivedAmount().longValue() * 100));
        params.put("vnp_OrderInfo", payment.getDescription());
        params.put("vnp_IpAddr", metadata.get("ipAddress").toString());

        ZonedDateTime createDate = payment.getCreatedAt().atZone(ZoneId.of(property.getTime().getZone()));
        params.put("vnp_CreateDate", createDate.format(getTimeFormatter()));
        ZonedDateTime expireDate = createDate.plusMinutes(property.getTime().getExpireMinute());
        params.put("vnp_ExpireDate", expireDate.format(getTimeFormatter()));

        return buildQueryString(params);
    }

    private Map<String, String> buildDepositDefault() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", property.getVersion());
        params.put("vnp_TmnCode", property.getTmnCode());
        params.put("vnp_CurrCode", property.getCurrencyCode());
        params.put("vnp_Command", property.getCommand());
        params.put("vnp_Locale", property.getLocale());
        params.put("vnp_ReturnUrl", property.getReturnUrl());
        params.put("vnp_OrderType", property.getOrderType());
        return params;
    }

    private DateTimeFormatter getTimeFormatter() {
        return DateTimeFormatter.ofPattern(property.getTime().getFormatter());
    }

    private String buildQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder rawBuilder = new StringBuilder();
        StringBuilder encodedBuilder = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        var charset = StandardCharsets.US_ASCII.toString();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if (StringUtils.hasLength(fieldValue)) {
                try {
                    //Build hash data
                    rawBuilder.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, charset));
                    //Build query
                    encodedBuilder.append(URLEncoder.encode(fieldName, charset)).append('=').append(URLEncoder.encode(fieldValue, charset));
                    if (itr.hasNext()) {
                        encodedBuilder.append('&');
                        rawBuilder.append('&');
                    }
                } catch (Exception e) {
                    throw new BadRequestException(e.getMessage(), e);
                }
            }
        }

        String signedValue = hmacSHA512(property.getHashSecret(), rawBuilder.toString());
        encodedBuilder.append("&vnp_SecureHash=").append(signedValue);

        return property.getUrl() + "?" + encodedBuilder;
    }

    private String hmacSHA512(final String key, final String data) {
        try {
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BadRequestException("Lỗi tạo chữ ký", e);
        }
    }
}
