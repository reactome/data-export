package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(Include.NON_NULL)
public class ProvenanceUrl {

    private String name;

    private String url;

    public ProvenanceUrl(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @JsonProperty(value = "nice_name", required = true)
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
