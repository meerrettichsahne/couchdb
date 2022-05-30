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
public class CItemRepository extends CouchDbRepositorySupport<CItem> {

    public CItemRepository(@Qualifier("onlineshopCouchDbConnector") CouchDbConnector db) {
        super(CItem.class, db);
        // this.db.createDatabaseIfNotExists();
        // this.initStandardDesignDocument();
    }

    @GenerateView
    public List<CItem> findByCategory(String category) {
        return queryView("by_category", category);
    }

    @Override
    @View(name = "all",
            map = "function(doc) {" +
                    "   if (doc.type === 'item') {" +
                    "       emit(doc._id, null);" +
                    "   }" +
                    "}")
    public List<CItem> getAll() {
        ViewQuery query = createQuery("all").includeDocs(true);
        return db.queryView(query, CItem.class);
    }
}
