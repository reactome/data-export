package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.reactome.server.export.opentargets.query.PathwayBase;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Evidence {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private List<Mutation> knownMutations = new ArrayList<>();
    private ResourceScore resourceScore = new ResourceScore();
    private ProvenanceType provenanceType;
    private List<ProvenanceUrl> urls;
    private String dateAsserted;
    private List<String> evidenceCodes;

    public Evidence(ReactomeEvidence reactomeEvidence) {
        if (reactomeEvidence.getMutations() != null) {
            for (String mutationName : reactomeEvidence.getMutations()) {
                this.knownMutations.add(new Mutation(mutationName));
            }
        }
        this.provenanceType = new ProvenanceType(reactomeEvidence.getPubMedIdentifiers());
        this.setProvenanceUrls(reactomeEvidence.getPathways());
        this.setDateAsserted(reactomeEvidence.getReleaseDate());
        this.setEvidenceCodes();
    }

    private void setProvenanceUrls(List<PathwayBase> pathways) {
        this.urls = new LinkedList<>();
        for (PathwayBase pathway : pathways) {
            this.urls.add(new ProvenanceUrl(pathway.getDisplayName(), "http://reactome.org/PathwayBrowser/#/" + pathway.getStId()));
        }
    }

    @JsonProperty(value = "known_mutations", required = true)
    public List<Mutation> getKnownMutations() {
        return knownMutations;
    }

    @JsonProperty(value = "resource_score", required = true)
    public ResourceScore getResourceScore() {
        return resourceScore;
    }

    @JsonProperty(value = "provenance_type", required = true)
    public ProvenanceType getProvenanceType() {
        return provenanceType;
    }

    public List<ProvenanceUrl> getUrls() {
        return urls;
    }

    @JsonProperty(value = "is_associated", required = true)
    public boolean isAssociated() {
        return true;
    }

    @JsonProperty(value = "evidence_codes", required = true)
    @JsonFormat(pattern = "^http://identifiers.org/eco/ECO:[0-9]{7,7}$")
    @JsonPropertyDescription("List of evidence codes in this format: http://identifiers.org/eco/ECO:nnnnnnn")
    public List<String> getEvidenceCodes() {
        return evidenceCodes;
    }

    @JsonProperty(value = "date_asserted", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public String getDateAsserted() {
        return dateAsserted;
    }

    private void setDateAsserted(String releaseDate) {
        Date dateAsserted = null;
        try {
            dateAsserted = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            //TODO: Error message
        }
        this.dateAsserted = formatter.format(dateAsserted);
    }

    private void setEvidenceCodes() {
        this.evidenceCodes = new LinkedList<>();
        this.evidenceCodes.add("http://purl.obolibrary.org/obo/ECO_0000205");
//        this.evidenceCodes.add(IDENTIFIERS_ORG + "/eco/ECO:0000205");

//        EXP = http://purl.obolibrary.org/obo/ECO_0000006
//        TAS = http://purl.obolibrary.org/obo/ECO_0000033
//        NAS = http://purl.obolibrary.org/obo/ECO_0000034
    }
}
