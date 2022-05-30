package de.marcorohn.seminar.couchdb;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class COrderRepository extends CouchDbRepositorySupport<COrder> {

    public COrderRepository(@Qualifier("onlineshopCouchDbConnector") CouchDbConnector db) {
        super(COrder.class, db);
        // this.db.createDatabaseIfNotExists();
        // this.initStandardDesignDocument();
    }

    @Override
    @View(name = "all",
            map = "function(doc) {" +
                    "   if (doc.type === 'order') {" +
                    "       emit(doc._id, null);" +
                    "   }" +
                    "}")
    public List<COrder> getAll() {
        ViewQuery query = createQuery("all").includeDocs(true);
        return db.queryView(query, COrder.class);
    }
}
