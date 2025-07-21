package com.xuanluan.practice.paygate.model.loader;

import com.univocity.parsers.annotations.Parsed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerBankCsv {
    @Parsed(field = "bank_code")
    private String bankCode;
    @Parsed(field = "payment_method_code")
    private String paymentMethodCode;
}
