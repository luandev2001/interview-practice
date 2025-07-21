package com.xuanluan.practice.paygate.service.shell;

import com.xuanluan.practice.paygate.model.entity.Bank;
import com.xuanluan.practice.paygate.model.entity.PartnerBank;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.model.loader.PartnerBankCsv;
import com.xuanluan.practice.paygate.repository.IBankRepository;
import com.xuanluan.practice.paygate.repository.IPartnerBankRepository;
import com.xuanluan.practice.paygate.repository.IPaymentMethodRepository;
import com.xuanluan.practice.paygate.repository.scope.BankSpec;
import com.xuanluan.practice.paygate.repository.scope.PaymentMethodSpec;
import com.xuanluan.practice.paygate.util.FileLoaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class PartnerBankShell {
    private final IPartnerBankRepository partnerBankRepository;
    private final IBankRepository bankRepository;
    private final IPaymentMethodRepository paymentMethodRepository;

    @ShellMethod(key = "partner_bank:import", value = "import partner_banks")
    public void importData() {
        String filePath = "db/seed/partner_banks.csv";
        List<PartnerBankCsv> allData = FileLoaderUtil.loadFromCsv(filePath, PartnerBankCsv.class);
        List<String> bankCodes = new ArrayList<>(allData.size());
        List<String> paymentMethodCodes = new ArrayList<>(allData.size());
        allData.forEach(item -> {
            bankCodes.add(item.getBankCode());
            paymentMethodCodes.add(item.getPaymentMethodCode());
        });

        // TODO: Mainly for testing with importing csv data so query is not optimized yet
        Map<String, Bank> bankById = bankRepository.findAll(BankSpec.activeWithCode(bankCodes))
                .stream()
                .collect(Collectors.toMap(Bank::getCode, bank -> bank));

        Map<String, PaymentMethod> paymentMethodById = paymentMethodRepository.findAll(PaymentMethodSpec.activeWithCode(paymentMethodCodes))
                .stream()
                .collect(Collectors.toMap(paymentMethod -> paymentMethod.getCode().name(), paymentMethod -> paymentMethod));

        Set<String> existingPairs = partnerBankRepository.findAll((root, query, cb) ->
                        cb.and(
                                root.get("bank").get("id").in(bankById.values().stream().map(Bank::getId).toList()),
                                root.get("paymentMethod").get("id").in(paymentMethodById.values().stream().map(PaymentMethod::getId).toList())
                        )
                )
                .stream()
                .map(pb -> pb.getBank().getId() + "_" + pb.getPaymentMethod().getId())
                .collect(Collectors.toSet());

        List<PartnerBank> newData = allData.stream()
                .map(csv -> {
                    Bank bank = bankById.get(csv.getBankCode());
                    PaymentMethod paymentMethod = paymentMethodById.get(csv.getPaymentMethodCode());
                    if (bank == null || paymentMethod == null) return null;

                    String key = bank.getId() + "_" + paymentMethod.getId();
                    if (existingPairs.contains(key)) return null;

                    PartnerBank partnerBank = new PartnerBank();
                    partnerBank.setBank(bank);
                    partnerBank.setPaymentMethod(paymentMethod);
                    return partnerBank;
                })
                .filter(Objects::nonNull)
                .toList();
        if (!newData.isEmpty()) {
            partnerBankRepository.saveAllAndFlush(newData);
            System.out.printf("Imported %d new payment methods\n", newData.size());

            String result = newData.stream()
                    .map(item -> String.format("(%s, %s)", item.getBank().getCode(), item.getPaymentMethod().getCode()))
                    .collect(Collectors.joining(", "));
            System.out.printf("Imported partner_banks: %s%n", result);
        } else {
            System.out.println("No new payment methods to import");
        }
    }
}
