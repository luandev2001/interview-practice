package com.xuanluan.practice.paygate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuanluan.practice.paygate.base.DataSetupTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = PaygateApplication.class)
@AutoConfigureMockMvc
public class PaygateApplicationTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected DataSetupTest setupTest;

    protected String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }
}
