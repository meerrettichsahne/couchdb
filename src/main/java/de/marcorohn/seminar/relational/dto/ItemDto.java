package de.marcorohn.seminar.relational.dto;

import de.marcorohn.seminar.relational.RCustomer;
import de.marcorohn.seminar.relational.RItem;
import de.marcorohn.seminar.relational.ROrder;

import java.util.List;
import java.util.stream.Collectors;

public class ItemDto {

    public long id;
    public String name;
    public String description;
    public String category;
    public double price;
    public List<Long> basketingCustomers;
    public List<Long> orders;

    public ItemDto() {
    }

    public ItemDto(RItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.category = item.getCategory();
        this.price = item.getPrice();
        this.orders = item.getOrders().stream().map(ROrder::getId).collect(Collectors.toList());
        this.basketingCustomers = item.getCustomersBasketingThis().stream().map(RCustomer::getId).collect(Collectors.toList());
    }

    public static ItemDto wrap(RItem item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(item);
    }
}
