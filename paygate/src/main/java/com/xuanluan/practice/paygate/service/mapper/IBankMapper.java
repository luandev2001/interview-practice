package com.xuanluan.practice.paygate.service.mapper;

import com.xuanluan.practice.paygate.model.entity.Bank;
import com.xuanluan.practice.paygate.model.loader.BankCsv;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBankMapper {
    Bank toBank(BankCsv bankCsv);
}
