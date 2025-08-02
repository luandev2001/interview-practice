package com.xuanluan.practice.paygate.base;

import com.xuanluan.practice.paygate.PaygateApplicationTest;
import com.xuanluan.practice.paygate.external.VNPayClient;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.property.VNPayProperty;
import com.xuanluan.practice.paygate.repository.IPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasePaymentTest extends PaygateApplicationTest {
    @Autowired
    protected IPaymentRepository paymentRepository;
    @Autowired
    protected VNPayProperty vnPayProperty;
    @MockitoSpyBean
    protected VNPayClient vnPayClient;

    protected void checkUrlWithVnPayProperty(String url) {
        assertTrue(url.startsWith(vnPayProperty.getUrl()));
        assertTrue(url.contains("vnp_SecureHash="));
        // Check required parameters from configuration
        assertTrue(url.contains("vnp_Version=" + vnPayProperty.getVersion()));
        assertTrue(url.contains("vnp_Command=" + vnPayProperty.getCommand()));
        assertTrue(url.contains("vnp_TmnCode=" + vnPayProperty.getTmnCode()));
        assertTrue(url.contains("vnp_CurrCode=" + vnPayProperty.getCurrencyCode()));
        assertTrue(url.contains("vnp_Locale=" + vnPayProperty.getLocale()));
        assertTrue(url.contains("vnp_OrderType=" + vnPayProperty.getOrderType()));
        assertTrue(url.contains("vnp_ReturnUrl=" + encode(vnPayProperty.getReturnUrl())));
    }

    protected void checkUrlWithPayment(String url, Payment payment) {
        var vnpOrderInfo = encode(payment.getDescription());
        var vnpAmount = payment.getAmount().multiply(BigDecimal.valueOf(100));
        assertTrue(url.contains("vnp_Amount=" + vnpAmount));
        assertTrue(url.contains("vnp_OrderInfo=" + vnpOrderInfo));
        assertTrue(url.contains("vnp_TxnRef=" + payment.getId()));

        // Verify date format matches configuration
        var timeProperty = vnPayProperty.getTime();
        assertEquals("yyyyMMddHHmmss", timeProperty.getFormatter());
        ZonedDateTime createDateZone = payment.getCreatedAt().atZone(ZoneId.of(timeProperty.getZone()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeProperty.getFormatter());

        String createDate = createDateZone.format(formatter);
        assertTrue(url.contains("vnp_CreateDate=" + createDate));
        String expireDate = createDateZone.plusMinutes(timeProperty.getExpireMinute()).format(formatter);
        assertTrue(url.contains("vnp_ExpireDate=" + expireDate));
    }
}
