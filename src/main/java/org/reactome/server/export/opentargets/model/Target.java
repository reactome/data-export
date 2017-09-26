package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Target {

    private TargetType targetType;

    private Activity activity;

    //Converted to a single string for version 1.2.4 (21/02/2017)
    private String identifier;

    public Target(ReactomeEvidence reactomeEvidence) {
        this.targetType = TargetType.Protein;
        this.activity = Activity.getActivity(reactomeEvidence.getActivity());
        this.identifier = "http://identifiers.org/" + reactomeEvidence.getResource().toLowerCase() + "/" + reactomeEvidence.getIdentifier();
    }

    @JsonProperty(value = "target_type", required = true)
    public String getTargetType() {
        return targetType.getContext();
    }

    @JsonProperty(value = "activity", required = true)
    public String getActivity() {
        return activity.getActivity();
    }

    @JsonProperty(value = "id", required = true)
    public String getIdentifier() {
        return identifier;
    }
}
