package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LiteratureReference {

    private String literatureId;

    public LiteratureReference(Integer pubmedId) {
        this.literatureId = "http://europepmc.org/abstract/MED/" + pubmedId;
    }

    @JsonProperty(value = "lit_id", required = true)
    public String getLiteratureId() {
        return literatureId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteratureReference that = (LiteratureReference) o;

        return !(literatureId != null ? !literatureId.equals(that.literatureId) : that.literatureId != null);

    }

    @Override
    public int hashCode() {
        return literatureId != null ? literatureId.hashCode() : 0;
    }
}
