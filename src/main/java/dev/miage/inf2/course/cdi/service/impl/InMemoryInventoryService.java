package dev.miage.inf2.course.cdi.service.impl;

import dev.miage.inf2.course.cdi.exception.OutOfStockException;
import dev.miage.inf2.course.cdi.model.Book;
import dev.miage.inf2.course.cdi.service.InventoryService;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Named;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("InventoryGoodForBookStore")
public class InMemoryInventoryService implements InventoryService<Book> {

    ConcurrentMap<String, BlockingDeque<Book>> inventory = new ConcurrentHashMap<>();

    @Override
    public void addToInventory(Book book) {
        synchronized (book.isbn()) {
            //System.out.println("Adding a new book to inventory, we have " + this.inventory.values().stream().mapToInt(i -> i.size()).sum() + " remaining");
            if (inventory.containsKey(book.isbn())) {
                inventory.get(book.isbn()).offer(book);
            } else {
                inventory.put(book.isbn(), new LinkedBlockingDeque<>(List.of(book)));
            }
        }
    }

    @Override
    public Book takeFromInventory() {
        try {
            //System.out.println("Taking a book from inventory, we have " + this.inventory.values().stream().mapToInt(i -> i.size()).sum() + " remaining");
            var book = inventory.values().stream().filter(v -> v.size() > 0).findAny().orElseThrow().poll();
            if (book == null) {
                throw new NoSuchElementException();
            }
            return book;
        } catch (NoSuchElementException e) {
            throw new OutOfStockException("we don't have any book for you");
        }
    }

    @Override
    public Book takeFromInventory(String id) {
        return this.inventory.get(id).poll();
    }

    @Override
    public long countItemsInInventory() {
        return inventory.entrySet().stream().mapToInt(e -> e.getValue().size()).sum();
    }

    @Override
    public Collection<Book> listAllItems() {
        return this.inventory.values().stream().flatMap(c -> c.stream()).collect(Collectors.toSet());
    }

    public void deleteBook(String isbn) {
        this.inventory.remove(isbn);
    }

}
