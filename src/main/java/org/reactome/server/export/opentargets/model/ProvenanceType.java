package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvenanceType {

    public ProvenanceDatabase database = new ProvenanceDatabase();

    public ProvenanceLiterature literature;

    public ProvenanceType(List<Integer> pubMedIdentifiers) {
        if (pubMedIdentifiers!=null && !pubMedIdentifiers.isEmpty()){
            this.literature = new ProvenanceLiterature(pubMedIdentifiers);
        }
    }
}
