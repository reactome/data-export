package org.reactome.server.export.opentargets.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ProvenanceLiterature {

    private Set<LiteratureReference> references = new HashSet<>();

    public ProvenanceLiterature(List<Integer> pubMedIdentifiers) {
        for (Integer pubMedIdentifier : pubMedIdentifiers) {
            references.add(new LiteratureReference(pubMedIdentifier));
        }
    }

    public Set<LiteratureReference> getReferences() {
        if(references.isEmpty()) return null;
        return references;
    }
}
