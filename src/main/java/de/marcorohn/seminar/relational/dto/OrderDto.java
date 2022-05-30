package de.marcorohn.seminar.relational.dto;

import de.marcorohn.seminar.relational.RItem;
import de.marcorohn.seminar.relational.ROrder;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDto {

    public long id;
    public long customer;
    public List<Long> items;
    public long submitted;

    public OrderDto() {
    }

    public OrderDto(ROrder order) {
        this.id = order.getId();
        this.customer = order.getCustomer().getId();
        this.items = order.getItems().stream().map(RItem::getId).collect(Collectors.toList());
        this.submitted = order.getSubmitted().getTime();
    }

    public static OrderDto wrap(ROrder order) {
        if (order == null) {
            return null;
        }
        return new OrderDto(order);
    }
}
