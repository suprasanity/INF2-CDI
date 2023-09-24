package dev.miage.inf2.course.cdi.service;

import java.util.Collection;

public interface InventoryService<T> {
    void addToInventory(T t);

    T takeFromInventory();

    T takeFromInventory(String id);

    long countItemsInInventory();

    Collection<T> listAllItems();

}
