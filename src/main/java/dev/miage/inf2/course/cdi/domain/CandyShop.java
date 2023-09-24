package dev.miage.inf2.course.cdi.domain;

import dev.miage.inf2.course.cdi.exception.OutOfStockException;
import dev.miage.inf2.course.cdi.model.Customer;
import dev.miage.inf2.course.cdi.model.Receipt;
import jakarta.enterprise.context.ApplicationScoped;
import dev.miage.inf2.course.cdi.model.Candy;
import jakarta.inject.Inject;
import jakarta.enterprise.event.Event;
import dev.miage.inf2.course.cdi.service.InventoryService;
import jakarta.inject.Named;
import dev.miage.inf2.course.cdi.service.ReceiptTransmissionService;

import java.util.Collection;
import java.util.Random;

@ApplicationScoped
public class CandyShop implements Shop<Candy>{
    @Inject
    Event<CandyCreatedEvent> candyCreatedEvent;

    @Inject
    @Named("InventoryGoodForCandyStore")
    protected InventoryService<Candy> inventoryService;

    @Inject
    @Named("ReceiptGoodForCandyStore")
    protected ReceiptTransmissionService<Candy> receiptTransmissionService;

    public CandyShop() {
    }

    public long countCandies() {
        return this.inventoryService.countItemsInInventory();
    }

    @Override
    public Candy sell(Customer customer) throws OutOfStockException {
        var soldCandy = this.inventoryService.takeFromInventory();
        Receipt<Candy> receipt = new Receipt<Candy>(soldCandy, new Random().nextInt(0, 30), 0.055);
        receiptTransmissionService.sendReceipt(customer, receipt);
        return soldCandy;
    }

    @Override
    public Candy sell(Customer customer, String id) {
        return this.inventoryService.takeFromInventory(id);
    }

    @Override
    public void stock(Candy candy) {
        this.inventoryService.addToInventory(candy);
        candyCreatedEvent.fire(new CandyCreatedEvent(candy));
    }

    @Override
    public Collection<Candy> getAllItems() {
        return this.inventoryService.listAllItems();
    }
}
