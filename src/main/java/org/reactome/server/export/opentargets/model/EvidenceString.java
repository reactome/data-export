package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.reactome.server.export.opentargets.query.PathwayBase;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Schema Migration - https://raw.githubusercontent.com/opentargets/json_schema/2.0.X/opentargets.json
 * @author Guilherme Viteri
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceString {

    @JsonProperty(value = "datasourceId", required = true)
    public String sourceID = "reactome";

    @JsonProperty(value = "datatypeId")
    public String type = "affected_pathway";

    @JsonProperty(value = "diseaseFromSource")
    public String diseaseFromSource;

    @JsonProperty(value = "diseaseFromSourceId")
    public String diseaseFromSourceId;

    @JsonProperty(value = "diseaseFromSourceMappedId")
    public String diseaseFromSourceMappedId;

    @JsonProperty(value = "functionalConsequenceId")
    public String functionalConsequenceId;

    @JsonProperty(value = "literature")
    public List<String> literature;

    @JsonProperty(value = "pathways")
    public List<Pathway> pathways = new ArrayList<>();

    @JsonProperty(value = "reactionId")
    public String reactionId;

    @JsonProperty(value = "reactionName")
    public String reactionName;

    @JsonProperty(value = "targetFromSourceId", required = true)
    public String targetFromSourceId;

    @JsonProperty(value = "targetModulation")
    public String targetModulation;

    @JsonProperty(value = "variantAminoacidDescriptions")
    public List<String> variantAminoacidDescriptions;

    public EvidenceString (ReactomeEvidence reactomeEvidence) {
        this.diseaseFromSource = reactomeEvidence.getDisease();
        this.diseaseFromSourceId = reactomeEvidence.getSourceDiseaseIdentifier();
        this.diseaseFromSourceMappedId = reactomeEvidence.getMappedDiseaseIdentifier();
        this.literature = reactomeEvidence.getPubMedIdentifiers();
        setPathways(reactomeEvidence.getPathways());
        this.reactionId = reactomeEvidence.getReaction().getStId();
        this.reactionName = reactomeEvidence.getReaction().getDisplayName();
        this.targetFromSourceId = reactomeEvidence.getIdentifier();
        this.targetModulation = reactomeEvidence.getActivity();
        this.variantAminoacidDescriptions = reactomeEvidence.getMutations();
    }

    private void setPathways(List<PathwayBase> pathwaysBase) {
        for (PathwayBase pathwayBase : pathwaysBase) {
            pathways.add(new Pathway(pathwayBase.getStId(), pathwayBase.getDisplayName()));
        }
    }

    @Override
    public String toString() {
        return "EvidenceString{" +
                "sourceID='" + sourceID + '\'' +
                ", type='" + type + '\'' +
                ", diseaseFromSource='" + diseaseFromSource + '\'' +
                ", diseaseFromSourceId='" + diseaseFromSourceId + '\'' +
                ", diseaseFromSourceMappedId='" + diseaseFromSourceMappedId + '\'' +
                ", functionalConsequenceId='" + functionalConsequenceId + '\'' +
                ", literature=" + literature +
                ", reactionId='" + reactionId + '\'' +
                ", targetFromSourceId='" + targetFromSourceId + '\'' +
                ", targetModulation='" + targetModulation + '\'' +
                ", variantAminoacidDescriptions=" + variantAminoacidDescriptions +
                '}';
    }
}
