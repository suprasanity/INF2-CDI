package dev.miage.inf2.course.cdi.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class CandyStockLogger {
    void onCandySold(@Observes CandyCreatedEvent candyCreatedEvent) {
        System.out.println("a new Candy " + candyCreatedEvent.getCandy().toString() + " has been created!");
    }
}
