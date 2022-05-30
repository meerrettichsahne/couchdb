package de.marcorohn.seminar.couchdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"id", "revision"})
public class CouchEntity {
    @Getter
    @Setter
    @JsonProperty("_id")
    private String id;

    @Getter
    @Setter
    @JsonProperty("_rev")
    private String revision;

    @Getter
    @Setter
    @JsonProperty("type")
    private String type;

    public CouchEntity() {
    }
}
