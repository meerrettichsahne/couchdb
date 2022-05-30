package de.marcorohn.seminar.relational;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ITEM")
@Table(name = "ITEM")
public class RItem {

    public RItem() {
    }

    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQ")
    @SequenceGenerator(name = "ID_SEQ_ITEM", allocationSize = 1)
    @Getter
    protected long id;

    @Getter
    @Setter
    @Column(name = "NAME")
    private String name;

    @Getter
    @Setter
    @Column(name = "DESCRIPTION")
    private String description;

    @Getter
    @Setter
    @Column(name = "CATEGORY")
    private String category;

    @Getter
    @Setter
    @Column(name = "PRICE")
    private double price;

    @Getter
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "basketItems", cascade = CascadeType.ALL)
    private List<RCustomer> customersBasketingThis = new ArrayList<>();

    @Getter
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "items", cascade = CascadeType.ALL)
    private List<ROrder> orders = new ArrayList<>();
}
