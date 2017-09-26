package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Association score is required. For curated databases the suggested values
 * to include by default are probability=1.0 and p-Value=0.0
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(Include.NON_NULL)
public class ResourceScore {

    public Double value = 1.0;

    public AssociationMethod method = new AssociationMethod();

    public String type = "probability";

}
