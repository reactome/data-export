package org.reactome.server.export.opentargets.query;

import org.reactome.server.export.opentargets.mapper.DiseaseMapper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ReactomeEvidence {

    private ReactionBase reaction;
    private String resource;        //Molecule resource: Uniprot | Ensembl | ChEBI
    private String identifier;      //Molecule identifier
    private List<String> mutations;
    private String doid;
    private String disease;
    private String diseaseResource;
    private String activity;
    private List<PathwayBase> pathways;
    private List<String> pubMedIdentifiers;

    public ReactomeEvidence() { }

    public ReactionBase getReaction() {
        return reaction;
    }

    public String getResource() {
        return resource;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<String> getMutations() {
        return (mutations == null) ? new ArrayList<>() : mutations;
    }

    public String getDisease() {
        return disease;
    }

    public String getActivity() {
        return activity;
    }

    public List<PathwayBase> getPathways() {
        return pathways;
    }

    public List<String> getPubMedIdentifiers() {
        return pubMedIdentifiers;
    }

    public String getMappedDiseaseIdentifier() {
        String res = DiseaseMapper.doidMapper.get(doid);
        String resource;
        String identifier;
        if (res == null) {
            resource = diseaseResource;
            identifier = doid;
        } else {
            String[] info = res.split(":");
            resource = info[0];
            identifier = info[1];
        }
        return resource + "_" + identifier;
    }

    public String getSourceDiseaseIdentifier() {
        return diseaseResource + ":" + doid;
    }
}
