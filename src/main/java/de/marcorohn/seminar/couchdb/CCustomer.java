package de.marcorohn.seminar.couchdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class CCustomer extends CouchEntity {

    @Getter
    @Setter
    @JsonProperty("username")
    private String username;

    @Getter
    @Setter
    @JsonProperty("name")
    private String name;

    @Getter
    @Setter
    @JsonProperty("basketItems")
    List<CItem> basketItems = new ArrayList<>();

    @Getter
    @Setter
    @JsonProperty("orders")
    List<COrder> orders = new ArrayList<>();

    public CCustomer() {
        this.setType("customer");
    }
}
