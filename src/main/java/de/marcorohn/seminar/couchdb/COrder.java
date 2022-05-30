package de.marcorohn.seminar.couchdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class COrder extends CouchEntity {

    @Getter
    @Setter
    @JsonProperty("items")
    private List<CItem> items = new ArrayList<>();

    @Getter
    @Setter
    @JsonProperty("submitted")
    private long submitted;

    public COrder() {
        this.setType("order");
    }
}
