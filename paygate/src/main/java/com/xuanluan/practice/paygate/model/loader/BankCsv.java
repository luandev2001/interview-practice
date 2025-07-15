package com.xuanluan.practice.paygate.model.loader;

import com.univocity.parsers.annotations.Parsed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankCsv {
    @Parsed(field = "name")
    private String name;
    @Parsed(field = "code")
    private String code;
    @Parsed(field = "bin")
    private String bin;
    @Parsed(field = "short_name")
    private String shortName;
    @Parsed(field = "swift_code")
    private String swiftCode;
    @Parsed(field = "logo")
    private String logo;
}
