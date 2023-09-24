package dev.miage.inf2.course.cdi.domain;
import dev.miage.inf2.course.cdi.model.Candy;

public class CandyCreatedEvent {
    final private Candy candy;

    public CandyCreatedEvent(Candy candy) {
        this.candy = candy;
    }

    public Candy getCandy() {
        return candy;
    }
}
