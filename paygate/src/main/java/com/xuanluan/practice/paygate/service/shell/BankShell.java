package com.xuanluan.practice.paygate.service.shell;

import com.xuanluan.practice.paygate.model.loader.BankCsv;
import com.xuanluan.practice.paygate.model.entity.Bank;
import com.xuanluan.practice.paygate.repository.IBankRepository;
import com.xuanluan.practice.paygate.repository.scope.BankSpec;
import com.xuanluan.practice.paygate.service.mapper.IBankMapper;
import com.xuanluan.practice.paygate.util.FileLoaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class BankShell {
    private final IBankRepository bankRepository;
    private final IBankMapper bankMapper;

    @ShellMethod(key = "bank:import", value = "Import banks")
    public void importData() {
        String fileName = "db/seed/banks.csv";
        List<BankCsv> allData = FileLoaderUtil.loadFromCsv(fileName, BankCsv.class);
        Set<String> codes = bankRepository.findBy(
                BankSpec.activeWithCode(allData.stream().map(BankCsv::getCode).toList()),
                query -> query.all().stream().map(Bank::getCode).collect(Collectors.toSet())
        );

        List<Bank> newData = allData.stream()
                .filter(method -> !codes.contains(method.getCode()))
                .map(bankMapper::toBank)
                .toList();
        if (!newData.isEmpty()) {
            bankRepository.saveAllAndFlush(newData);
            System.out.printf("Imported %d new banks\n", newData.size());
            System.out.printf(
                    "Imported banks with codes: %s \n",
                    newData.stream().map(Bank::getCode).collect(Collectors.toList())
            );
        } else {
            System.out.println("No new banks to import");
        }
    }
}
