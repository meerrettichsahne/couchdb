package de.marcorohn.seminar.relational.dto;

import de.marcorohn.seminar.relational.RCustomer;
import de.marcorohn.seminar.relational.RItem;
import de.marcorohn.seminar.relational.ROrder;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerDto {

    public long id;
    public String name;
    public String username;
    public List<Long> basketItems;
    public List<Long> orders;

    public CustomerDto() {
    }

    public CustomerDto(RCustomer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.username = customer.getUsername();
        this.basketItems = customer.getBasketItems().stream().map(RItem::getId).collect(Collectors.toList());
        this.orders = customer.getOrders().stream().map(ROrder::getId).collect(Collectors.toList());
    }

    public static CustomerDto wrap(RCustomer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerDto(customer);
    }
}
