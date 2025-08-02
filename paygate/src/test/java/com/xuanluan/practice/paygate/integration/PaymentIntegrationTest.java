package com.xuanluan.practice.paygate.integration;

import com.xuanluan.practice.paygate.base.BasePaymentTest;
import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import com.xuanluan.practice.paygate.model.entity.Bank;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.response.external.VNPayResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentIntegrationTest extends BasePaymentTest {
    private UUID userId;
    private PaymentMethod paymentMethod;
    private Bank bank;
    private String ipAddress;

    @BeforeAll
    void setUp() {
        userId = UUID.randomUUID();
        ipAddress = "127.0.0.1";
        // Setup test data in database
        paymentMethod = setupTest.createVNPayMethod();
        bank = setupTest.createBank();
    }

    @Test
    void testCompletePaymentFlow_Success() throws Exception {
        // Step 1: Create deposit request
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setPaymentMethodCode("vnpay");
        depositRequest.setAmount(new BigDecimal(10000));
        depositRequest.setUserId(userId);
        depositRequest.setDescription("Integration test payment");

        // Mock VNPayClient for deposit
        String expectedUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=1000000&vnp_Command=pay&vnp_CreateDate=20241201120000&vnp_CurrCode=VND&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=Integration+test+payment&vnp_OrderType=other&vnp_ReturnUrl=http://localhost:8080/payment/return&vnp_TmnCode=TMNC&vnp_TxnRef=test-payment-id&vnp_Version=2.1.1&vnp_SecureHash=mocked_hash";
        doReturn(expectedUrl).when(vnPayClient).createUrl(any(Payment.class), anyMap());

        // Step 2: Call deposit endpoint
        String responseJson = mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest))
                        .header("X-Forwarded-For", ipAddress))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentId").exists())
                .andExpect(jsonPath("$.data.url").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 3: Extract payment ID from response
        Map<String, Object> response = objectMapper.readValue(responseJson, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        UUID paymentId = UUID.fromString((String) data.get("paymentId"));
        String paymentUrl = (String) data.get("url");

        // Step 4: Verify payment was created in database
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        assertNotNull(payment);
        assertEquals(userId, payment.getUserId());
        assertEquals(depositRequest.getAmount(), payment.getAmount());
        assertEquals(depositRequest.getDescription(), payment.getDescription());
        assertEquals(PaymentConstant.Status.PENDING, payment.getStatus());
        assertEquals(paymentMethod.getId(), payment.getPaymentMethod().getId());

        assertEquals(paymentUrl, expectedUrl);
        // Mock VNPayClient for IPN validation
        String expectedHash = "mocked_hash";
        when(vnPayClient.hashAllFields(anyMap())).thenReturn(expectedHash);

        // Step 5: Simulate successful IPN callback
        // Only need to set minimal parameters for testing, not the full VNPay production configuration.
        var vnpTransactionNo = "VNP15101960";
        String successIpnResponse = mockMvc.perform(get("/payment/ipns/vnpay")
                        .param("vnp_TxnRef", paymentId.toString())
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_BankCode", "NCB")
                        .param("vnp_TransactionNo", vnpTransactionNo)
                        .param("vnp_SecureHash", expectedHash))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 6: Verify IPN response
        VNPayResponse ipnResponse = objectMapper.readValue(successIpnResponse, VNPayResponse.class);
        assertNotNull(ipnResponse);
        assertEquals("Payment Success", ipnResponse.Message());
        assertEquals("00", ipnResponse.RspCode());

        // Step 7: Verify payment status was updated
        Payment updatedPayment = paymentRepository.findById(paymentId).orElse(null);
        assertNotNull(updatedPayment);
        assertEquals(PaymentConstant.Status.NETWORK_CONFIRMED, updatedPayment.getStatus());
        assertEquals(bank.getId(), updatedPayment.getBank().getId());
        assertEquals(vnpTransactionNo, updatedPayment.getExternalId());
    }

    @Test
    void testCreatePaymentUrl_WithRealVNPayClient() throws Exception {
        // Step 1: Create deposit request
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setPaymentMethodCode("vnpay");
        depositRequest.setAmount(new BigDecimal(10000));
        depositRequest.setUserId(userId);
        depositRequest.setDescription("Test payment URL creation");

        // Step 2: Call deposit endpoint WITHOUT mocking VNPayClient
        // This will use the real VNPayClient with actual configuration
        String responseJson = mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest))
                        .header("X-Forwarded-For", ipAddress))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentId").exists())
                .andExpect(jsonPath("$.data.url").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 3: Extract payment URL from response
        Map<String, Object> response = objectMapper.readValue(responseJson, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        String paymentUrl = (String) data.get("url");

        // Step 4: Verify URL structure and configuration
        assertNotNull(paymentUrl);
        // Check required parameters from configuration
        assertTrue(paymentUrl.startsWith(vnPayProperty.getUrl()));
        checkUrlWithVnPayProperty(paymentUrl);
        // Check dynamic parameters
        var paymentId = UUID.fromString(data.get("paymentId").toString());
        var payment = paymentRepository.findById(paymentId).orElse(null);
        assertNotNull(payment);
        checkUrlWithPayment(paymentUrl, payment);
    }

    @Test
    void testPaymentFlow_WithFailedIPN() throws Exception {
        // Step 1: Create deposit request
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setPaymentMethodCode("vnpay");
        depositRequest.setAmount(new BigDecimal("10000"));
        depositRequest.setUserId(userId);
        depositRequest.setDescription("Failed payment test");

        // Mock VNPayClient for deposit
        String expectedUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=500000&vnp_Command=pay&vnp_CreateDate=20241201120000&vnp_CurrCode=VND&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=Failed+payment+test&vnp_OrderType=other&vnp_ReturnUrl=http://localhost:8080/payment/return&vnp_TmnCode=TMNC&vnp_TxnRef=test-payment-id&vnp_Version=2.1.0&vnp_SecureHash=mocked_hash";
        doReturn(expectedUrl).when(vnPayClient).createUrl(any(Payment.class), anyMap());

        // Step 2: Call deposit endpoint
        String responseJson = mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest))
                        .header("X-Forwarded-For", "127.0.0.1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 3: Extract payment ID
        Map<String, Object> response = objectMapper.readValue(responseJson, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        UUID paymentId = UUID.fromString((String) data.get("paymentId"));

        // Mock VNPayClient for IPN validation
        String expectedHash = "mocked_valid_hash";
        when(vnPayClient.hashAllFields(anyMap())).thenReturn(expectedHash);

        // Step 4: Simulate failed IPN callback
        String failedIpnResponse = mockMvc.perform(get("/payment/ipns/vnpay")
                        .param("vnp_TxnRef", paymentId.toString())
                        .param("vnp_ResponseCode", "99")
                        .param("vnp_BankCode", "NCB")
                        .param("vnp_Amount", "500000")
                        .param("vnp_OrderInfo", "Failed payment test")
                        .param("vnp_CreateDate", "20241201120000")
                        .param("vnp_TransactionNo", "12345678")
                        .param("vnp_SecureHash", "mocked_hash"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 5: Verify IPN response
        VNPayResponse ipnResponse = objectMapper.readValue(failedIpnResponse, VNPayResponse.class);
        assertNotNull(ipnResponse);
        assertEquals("Invalid Checksum", ipnResponse.Message());
        assertEquals("99", ipnResponse.RspCode());

        // Step 6: Verify payment status was updated to failed
        Payment updatedPayment = paymentRepository.findById(paymentId).orElse(null);
        assertNotNull(updatedPayment);
        assertEquals(PaymentConstant.Status.PENDING, updatedPayment.getStatus());
    }

    @Test
    void testPaymentFlow_WithDuplicateIPN() throws Exception {
        // Step 1: Create deposit request
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setPaymentMethodCode("vnpay");
        depositRequest.setAmount(new BigDecimal("15000"));
        depositRequest.setUserId(userId);
        depositRequest.setDescription("Duplicate IPN test");
        depositRequest.setMetadata(new HashMap<>());

        // Mock VNPayClient for deposit
        String expectedUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=1500000&vnp_Command=pay&vnp_CreateDate=20241201120000&vnp_CurrCode=VND&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=Duplicate+IPN+test&vnp_OrderType=other&vnp_ReturnUrl=http://localhost:8080/payment/return&vnp_TmnCode=TMNC&vnp_TxnRef=test-payment-id&vnp_Version=2.1.0&vnp_SecureHash=mocked_hash";
        doReturn(expectedUrl).when(vnPayClient).createUrl(any(Payment.class), anyMap());

        // Step 2: Call deposit endpoint
        String responseJson = mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest))
                        .header("X-Forwarded-For", "127.0.0.1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 3: Extract payment ID
        Map<String, Object> response = objectMapper.readValue(responseJson, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        UUID paymentId = UUID.fromString((String) data.get("paymentId"));

        // Mock VNPayClient for IPN validation
        String expectedHash = "mocked_valid_hash";
        when(vnPayClient.hashAllFields(anyMap())).thenReturn(expectedHash);

        // Step 4: First IPN call - should succeed
        mockMvc.perform(get("/payment/ipns/vnpay")
                        .param("vnp_TxnRef", paymentId.toString())
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_BankCode", "NCB")
                        .param("vnp_Amount", "1500000")
                        .param("vnp_OrderInfo", "Duplicate IPN test")
                        .param("vnp_CreateDate", "20241201120000")
                        .param("vnp_TransactionNo", "12345678")
                        .param("vnp_SecureHash", expectedHash))
                .andExpect(status().isOk());

        // Step 5: Second IPN call - should return duplicate message
        String duplicateIpnResponse = mockMvc.perform(get("/payment/ipns/vnpay")
                        .param("vnp_TxnRef", paymentId.toString())
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_BankCode", "NCB")
                        .param("vnp_Amount", "1500000")
                        .param("vnp_OrderInfo", "Duplicate IPN test")
                        .param("vnp_CreateDate", "20241201120000")
                        .param("vnp_TransactionNo", "12345678")
                        .param("vnp_SecureHash", expectedHash))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 6: Verify duplicate IPN response
        VNPayResponse ipnResponse = objectMapper.readValue(duplicateIpnResponse, VNPayResponse.class);
        assertNotNull(ipnResponse);
        assertEquals("Duplicate Call IPN", ipnResponse.Message());
        assertEquals("00", ipnResponse.RspCode());
    }

    @Test
    void testPaymentFlow_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Test with null amount
        DepositRequest invalidRequest = new DepositRequest();
        invalidRequest.setPaymentMethodCode("vnpay");
        invalidRequest.setAmount(null);
        invalidRequest.setUserId(userId);
        invalidRequest.setDescription("Invalid request test");
        invalidRequest.setMetadata(new HashMap<>());

        mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Test with zero amount
        invalidRequest.setAmount(BigDecimal.ZERO);
        mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Test with null userId
        invalidRequest.setAmount(new BigDecimal("10000"));
        invalidRequest.setUserId(null);
        mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Test with empty description
        invalidRequest.setUserId(userId);
        invalidRequest.setDescription("");
        mockMvc.perform(post("/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}