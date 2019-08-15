package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceString {

    @SuppressWarnings("unused")
    public String type = "affected_pathway";

    @SuppressWarnings("unused")
    public String sourceID = "reactome";

    @SuppressWarnings("unused")
    @JsonProperty(value = "validated_against_schema_version", required = true)
    public String version = "1.3.0";

    @SuppressWarnings("unused")
    @JsonProperty(value = "access_level", required = true)
    public String accessLevel = "public";

    @JsonProperty(value = "unique_association_fields", required = true)
    public UniqueAssociationFields uniqueAssociationFields;

    public Target target;

    @JsonProperty(value = "disease", required = true)
    public EvidenceDisease evidenceDisease;


    // Removed on demand for release 19.08 (14/08/2019)
    //public ProvenanceLiterature literature;

    public Evidence evidence;

    public EvidenceString(ReactomeEvidence reactomeEvidence) {
        this.evidence = new Evidence(reactomeEvidence);
        this.evidenceDisease = new EvidenceDisease(reactomeEvidence);
        this.uniqueAssociationFields = new UniqueAssociationFields(reactomeEvidence);
        this.target = new Target(reactomeEvidence);
//        if (reactomeEvidence.getPubMedIdentifiers() != null && !reactomeEvidence.getPubMedIdentifiers().isEmpty()) {
//            // Removed on demand for release 19.08 (14/08/2019)
//            this.literature = new ProvenanceLiterature(reactomeEvidence.getPubMedIdentifiers());
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EvidenceString that = (EvidenceString) o;

        return !(uniqueAssociationFields != null ? !uniqueAssociationFields.equals(that.uniqueAssociationFields) : that.uniqueAssociationFields != null);

    }

    @Override
    public int hashCode() {
        return uniqueAssociationFields != null ? uniqueAssociationFields.hashCode() : 0;
    }
}
