package de.marcorohn.seminar.couchdb;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchDbConfig {
    private HttpClient httpClient;
    private CouchDbInstance instance;
    private CouchDbConnector connector;

    public CouchDbConfig() {
        this.httpClient = new StdHttpClient.Builder()
                .enableSSL(true)
                .host("couchdb.tunfis.ch")
                .port(443)
                .username("admin")
                .password("nyM7rRqoHFD7Vm4Y8mhyXpHStQ2gvVAi")
                .build();
        this.instance = new StdCouchDbInstance(httpClient);
    }

    @Bean
    public CouchDbConnector onlineshopCouchDbConnector(CouchDbInstance couchdbInstance) {
        return new StdCouchDbConnector("onlineshop", couchdbInstance);
    }

    @Bean
    public CouchDbInstance couchDbInstance() {
        return this.instance;
    }
}
