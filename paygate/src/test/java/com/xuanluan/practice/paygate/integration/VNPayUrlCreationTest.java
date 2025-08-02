package com.xuanluan.practice.paygate.integration;

import com.xuanluan.practice.paygate.base.BasePaymentTest;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VNPayUrlCreationTest extends BasePaymentTest {
    private Payment payment;
    private Map<String, Object> metadata;

    @BeforeAll
    void setUp() {
        PaymentMethod paymentMethod = setupTest.buildPaymentMethod();
        payment = setupTest.buildPayment();
        payment.setId(UUID.randomUUID());
        payment.setPaymentMethod(paymentMethod);

        // Setup metadata
        metadata = new HashMap<>();
        metadata.put("ipAddress", "127.0.0.1");
    }

    @Test
    void testCreateUrl_WithRealConfiguration() {
        // When
        String paymentUrl = vnPayClient.createUrl(payment, metadata);

        // Then
        assertNotNull(paymentUrl);
        checkUrlWithVnPayProperty(paymentUrl);
        checkUrlWithPayment(paymentUrl, payment);

        // Verify secure hash exists and is not empty
        int secureIndex = paymentUrl.lastIndexOf("vnp_SecureHash=");
        assertTrue(secureIndex != -1);
        String secureHash = paymentUrl.substring(secureIndex + "vnp_SecureHash=".length());
        assertTrue(StringUtils.hasLength(secureHash));
        assertTrue(secureHash.matches("^[a-f0-9]+$"));
    }

    @Test
    void testCreateUrl_WithSpecialCharactersInDescription() {
        // Given
        payment.setDescription("Test payment with special chars: @#$%^&*()");

        // When
        String paymentUrl = vnPayClient.createUrl(payment, metadata);

        // Then
        // Should be URL encoded
        assertFalse(paymentUrl.contains("@#$%^&*()"));
        assertTrue(paymentUrl.contains("vnp_OrderInfo=" + encode(payment.getDescription())));
    }

    @Test
    void testHashAllFields_WithRealConfiguration() {
        // Given
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Amount", "1000000");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_OrderInfo", "Test payment");
        params.put("vnp_TxnRef", payment.getId().toString());
        params.put("vnp_ReturnUrl", vnPayProperty.getReturnUrl());
        params.put("vnp_TmnCode", vnPayProperty.getTmnCode());
        params.put("vnp_Version", vnPayProperty.getVersion());
        params.put("vnp_Command", vnPayProperty.getCommand());
        params.put("vnp_CurrCode", vnPayProperty.getCurrencyCode());
        params.put("vnp_Locale", vnPayProperty.getLocale());
        params.put("vnp_OrderType", vnPayProperty.getOrderType());

        // When
        String result = vnPayClient.hashAllFields(params);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Hash should be a valid hex string
        assertTrue(result.matches("^[a-f0-9]+$"));
    }
} 