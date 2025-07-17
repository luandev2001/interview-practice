package com.xuanluan.practice.paygate.service.shell;

import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.repository.IPaymentMethodRepository;
import com.xuanluan.practice.paygate.repository.scope.PaymentMethodSpec;
import com.xuanluan.practice.paygate.util.FileLoaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class PaymentMethodShell {
    private final IPaymentMethodRepository paymentMethodRepository;

    @Transactional
    @ShellMethod(key = "payment_method:import", value = "Import payment_methods")
    public void importData() {
        String fileName = "db/seed/payment_methods.json";
        List<PaymentMethod> allData = FileLoaderUtil.loadFromJson(fileName, PaymentMethod.class);

        Set<String> codes = paymentMethodRepository.findAll(PaymentMethodSpec.activeWithCode(allData.stream().map(PaymentMethod::getCode).toList())).stream()
                .map(PaymentMethod::getCode).collect(Collectors.toSet());

        List<PaymentMethod> newData = allData.stream().filter(method -> !codes.contains(method.getCode())).toList();
        if (!newData.isEmpty()) {
            paymentMethodRepository.saveAllAndFlush(newData);
            System.out.printf("Imported %d new payment methods\n", newData.size());
            System.out.printf(
                    "Imported payment methods with codes: %s \n",
                    newData.stream().map(PaymentMethod::getCode).toList()
            );
        } else {
            System.out.println("No new payment methods to import");
        }
    }
}
