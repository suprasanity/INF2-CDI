package dev.miage.inf2.course.cdi.domain;

import dev.miage.inf2.course.cdi.domain.BookShop;
import dev.miage.inf2.course.cdi.domain.Shop;
import dev.miage.inf2.course.cdi.exception.OutOfStockException;
import dev.miage.inf2.course.cdi.model.Book;
import dev.miage.inf2.course.cdi.model.Customer;
import dev.miage.inf2.course.cdi.service.InventoryService;
import dev.miage.inf2.course.cdi.service.ReceiptTransmissionService;
import info.schnatterer.mobynamesgenerator.MobyNamesGenerator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BookShopTest {


    static Customer customer;

    @BeforeEach
    private void setup() {
        customer = new Customer(MobyNamesGenerator.getRandomName(), MobyNamesGenerator.getRandomName(), "toto@miage.dev", "+333895457896",12);
    }

    @Inject
    BookShop bookShop;


    @BeforeEach
    void setUp() {
        try {
            while (this.bookShop.sell(customer) != null) {
                // keep selling what we have
            }
        } catch (OutOfStockException oos) {
            // ok, the bookshop is empty now
        }

    }

    @Test
    void sellAnStockTest() throws InterruptedException {

        var bookCount = 10;
        var copyPerBook = 10;
        var salesCount = 50;


        getStokingRunnable(bookShop, bookCount, copyPerBook).forEach(Runnable::run);
        getSellingRunnable(bookShop, salesCount).forEach(Runnable::run);

        assertEquals(bookCount * copyPerBook - salesCount, bookShop.countBooks());


    }


    @Test
    void outOfStock() throws InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(12);
        getStokingRunnable(bookShop, 10, 10).parallel().forEach(Runnable::run);

        try {
            getSellingRunnable(bookShop, 101).parallel().forEach(Runnable::run);
            fail("should have thrown");

        } catch (OutOfStockException rte) {
            //for the win

        }

    }


    private static Book getRandomBook() {
        return new Book(MobyNamesGenerator.getRandomName(), "the story of " + MobyNamesGenerator.getRandomName(), getRandomISBN());
    }

    private static String getRandomISBN() {
        var random = new Random();
        return String.format("%03d-%01d-%02d-%06d-%01d", random.nextInt(1000), random.nextInt(10), random.nextInt(100), random.nextInt(1000000), random.nextInt(10));
    }

    private static Stream<Runnable> getSellingRunnable(Shop<Book> bookShop, int bookCount) {

        return IntStream.range(0, bookCount).mapToObj(i -> () -> bookShop.sell(customer));

    }


    private static Stream<Runnable> getStokingRunnable(Shop<Book> bookShop, int bookCount, int copyPerBookCount) {
        return IntStream.range(0, bookCount).mapToObj(i -> {
            var randomBook = getRandomBook();
            return IntStream.range(0, copyPerBookCount).mapToObj(ii -> (Runnable) () -> {
                bookShop.stock(randomBook);

            });
        }).flatMap(Function.identity());
    }


}