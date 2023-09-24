package dev.miage.inf2.course.cdi.service.impl;

import dev.miage.inf2.course.cdi.exception.OutOfStockException;
import dev.miage.inf2.course.cdi.model.Candy;
import dev.miage.inf2.course.cdi.service.InventoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
//import map
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("InventoryGoodForCandyStore")
public class InMemoryInventoryCandyService implements InventoryService<Candy> {

    ConcurrentMap<String, BlockingDeque<Candy>> inventory = new ConcurrentHashMap<>();

    @Override
    public void addToInventory(Candy candy) {
        synchronized (candy.id()) {
            if (inventory.containsKey(candy.id())) {
                inventory.get(candy.id()).offer(candy);
            } else {
                inventory.put(candy.id(), new LinkedBlockingDeque<>(List.of(candy)));
            }
        }
    }

    @Override
    public Candy takeFromInventory() {
        try {
            var candy = inventory.values().stream().filter(v -> v.size() > 0).findAny().orElseThrow().poll();
            if (candy == null) {
                throw new NoSuchElementException();
            }
            return candy;
        } catch (NoSuchElementException e) {
            throw new OutOfStockException("we don't have any candy for you");
        }
    }

    @Override
    public Candy takeFromInventory(String id) {
        return this.inventory.get(id).poll();
    }

    @Override
    public long countItemsInInventory() {
        return inventory.entrySet().stream().mapToInt(e -> e.getValue().size()).sum();
    }

    public Collection<Candy> listAllItems() {
        Map<String, Integer> candyWeightSumMap = new HashMap<>();


        for (BlockingDeque<Candy> candyDeque : inventory.values()) {
            for (Candy candy : candyDeque) {
                String candyId = candy.id();
                int candyWeight = candy.weight();


                candyWeightSumMap.merge(candyId, candyWeight, Integer::sum);
            }
        }
        Collection<Candy> result = candyWeightSumMap.entrySet().stream()
                .map(entry -> new Candy(entry.getKey(), getCandyFlavor(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        return result;
    }

    private String getCandyFlavor(String candyId) {
        //shouldn't return npe since flavor is always set
     return this.inventory.get(candyId).peek().flavor();
    }



}
