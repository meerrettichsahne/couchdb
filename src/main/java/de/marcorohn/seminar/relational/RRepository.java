package de.marcorohn.seminar.relational;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Service
public class RRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(RCustomer customer){
        if (em.contains(customer)) {
            em.merge(customer);
        } else {
            em.persist(customer);
        }
    }

    public void save(RItem item){
        if (em.contains(item)) {
            em.merge(item);
        } else {
            em.persist(item);
        }
    }

    public void save(ROrder order){
        if (em.contains(order)) {
            em.merge(order);
        } else {
            em.persist(order);
        }
    }

    public RCustomer findCustomer(long id) {
        return em.find(RCustomer.class, id);
    }

    public RItem findItem(long id) {
        return em.find(RItem.class, id);
    }

    public ROrder findOrder(long id) {
        return em.find(ROrder.class, id);
    }

    public RCustomer findCustomerByUsername(String username) {
        final String query = "select c from CUSTOMER c where c.username = :username";
        TypedQuery<RCustomer> typedQuery = em.createQuery(query, RCustomer.class);
        typedQuery.setParameter("username", username);
        List<RCustomer> rl =  typedQuery.getResultList();
        if (rl.size() == 0) {
            return null;
        } else {
            return rl.get(0);
        }
    }

    public List<RItem> findAllItems() {
        final String query = "select i from ITEM i";
        TypedQuery<RItem> typedQuery = em.createQuery(query, RItem.class);
        return typedQuery.getResultList();
    }

    public List<RItem> findAllItemsInCategory(String category) {
        final String query = "select i from ITEM i where i.category = :category";
        TypedQuery<RItem> typedQuery = em.createQuery(query, RItem.class);
        typedQuery.setParameter("category", category);
        return typedQuery.getResultList();
    }

    @Transactional
    public RCustomer createCustomer(String username, String displayName) {
        RCustomer customer = new RCustomer();
        customer.setUsername(username);
        customer.setName(displayName);
        this.save(customer);
        return customer;
    }

    @Transactional
    public RItem createItem(String displayName, String description, String category, double price) {
        RItem item = new RItem();
        item.setName(displayName);
        item.setDescription(description);
        item.setCategory(category);
        item.setPrice(price);
        this.save(item);
        return item;
    }

    @Transactional
    public ROrder createOrder(Date submitted, RCustomer customer, List<RItem> items) {
        ROrder order = new ROrder();
        order.setCustomer(customer);
        order.setSubmitted(submitted);
        this.save(order);
        order.getItems().addAll(items);
        return order;
    }

    @Transactional
    public void deleteCustomer(RCustomer customer) {
        this.em.remove(customer);
    }

    @Transactional
    public void deleteItem(RItem item) {
        item.getCustomersBasketingThis().forEach(customer -> customer.getBasketItems().remove(item));
        item.getOrders().forEach(order -> order.getItems().remove(item));
        this.em.remove(item);
    }

    @Transactional
    public void deleteOrder(ROrder order) {
        order.getCustomer().getOrders().remove(order);
        order.getItems().forEach(item -> item.getOrders().remove(order));
        this.em.remove(order);
    }

    public Double getCustomerBasketSum(String username) {
        Query query = em.createNativeQuery("SELECT sum(item.price) FROM item\n" +
                "    JOIN userbasket_items bi on item.id = bi.item_id\n" +
                "    JOIN customer c on bi.user_id = c.id\n" +
                "WHERE c.username = ?;");
        query.setParameter(1, username);
        return (Double) query.getSingleResult();
    }
}
