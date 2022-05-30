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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "USER_ORDER")
@Table(name = "USER_ORDER")
public class ROrder {

    public ROrder() {
    }

    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQ")
    @SequenceGenerator(name = "ID_SEQ_ORDER", allocationSize = 1)
    @Getter
    protected long id;

    @Getter
    @Setter
    @JoinColumn(name = "BUYER")
    @ManyToOne(fetch = FetchType.LAZY)
    private RCustomer customer;

    @Getter
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "ORDERS_ITEMS",
            joinColumns = @JoinColumn(name = "ORDER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
    private List<RItem> items = new ArrayList<>();

    @Getter
    @Setter
    @Column(name = "SUBMITTED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submitted;
}
