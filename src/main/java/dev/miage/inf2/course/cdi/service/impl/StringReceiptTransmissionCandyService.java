package dev.miage.inf2.course.cdi.service.impl;

import dev.miage.inf2.course.cdi.model.Book;
import dev.miage.inf2.course.cdi.model.Candy;
import dev.miage.inf2.course.cdi.model.Customer;
import dev.miage.inf2.course.cdi.model.Receipt;
import dev.miage.inf2.course.cdi.service.ReceiptTransmissionService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

@Dependent
@Named("ReceiptGoodForCandyStore")
public class StringReceiptTransmissionCandyService implements ReceiptTransmissionService<Candy> {

    private static final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void sendReceipt(Customer customer, Receipt receipt) {
        stringBuilder.append("Merci d'avoir acheté " + receipt.item().toString() + " pour un montant de " + receipt.price()).append("\n");
    }
}
