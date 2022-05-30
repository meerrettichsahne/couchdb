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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "CUSTOMER")
@Table(name = "CUSTOMER")
public class RCustomer {

    public RCustomer() {
    }

    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQ")
    @SequenceGenerator(name = "ID_SEQ_CUSTOMER", allocationSize = 1)
    @Getter
    protected long id;

    @Getter
    @Setter
    @Column(name = "USERNAME")
    private String username;

    @Getter
    @Setter
    @Column(name = "NAME")
    private String name;

    @Getter
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "USERBASKET_ITEMS",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
    private List<RItem> basketItems = new ArrayList<>();

    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private List<ROrder> orders = new ArrayList<>();
}
