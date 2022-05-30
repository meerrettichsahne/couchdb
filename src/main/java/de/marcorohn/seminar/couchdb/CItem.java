package de.marcorohn.seminar.couchdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class CItem extends CouchEntity {

    @Getter
    @Setter
    @JsonProperty("name")
    private String name;

    @Getter
    @Setter
    @JsonProperty("description")
    private String description;

    @Getter
    @Setter
    @JsonProperty("category")
    private String category;

    @Getter
    @Setter
    @JsonProperty("price")
    private double price;

    public CItem() {

    }
}
