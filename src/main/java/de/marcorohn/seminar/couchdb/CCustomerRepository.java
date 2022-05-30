package de.marcorohn.seminar.couchdb;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CCustomerRepository extends CouchDbRepositorySupport<CCustomer> {

    public CCustomerRepository(@Qualifier("onlineshopCouchDbConnector") CouchDbConnector db) {
        super(CCustomer.class, db);
        this.db.createDatabaseIfNotExists();
        this.initStandardDesignDocument();
    }

    @GenerateView
    public CCustomer findByUsername(String username) {
        List<CCustomer> rl = queryView("by_username", username);
        if (rl.size() == 0) {
            return null;
        }
        return rl.get(0);
    }

    @View(name = "customerBasket",
            map = "function(doc) {" +
            "   if (doc.type === 'customer') {" +
                "   for (var item in doc.basketItems) {" +
            "           emit(doc.username, item);" +
            "       }" +
            "   }" +
            "}")
    public List<CItem> getCustomerBasket(String username) {
        ViewQuery query = createQuery("customerBasket").key(username);
        return db.queryView(query, CItem.class);
    }

    @View(name = "customerBasketSum",
            map = "function(doc) {" +
                    "if (doc.type === 'customer') {" +
                    "   var sum = 0;" +
                    "   for (var item in doc.basketItems) {" +
                    "       sum = sum + item.price;" +
                    "   }" +
                    "   emit(doc.username, sum);" +
                    "}}")
    public Double getCustomerBasketSum(String username) {
        ViewQuery query = createQuery("customerBasket").key(username);
        List<Double> rl = db.queryView(query, Double.class);
        if (rl.size() == 0) {
            return 0.0;
        }
        return rl.get(0);
    }

}
