package org.reactome.server.export.opentargets.query;

import org.reactome.server.export.opentargets.mapper.DiseaseMapper;

import java.util.List;

@SuppressWarnings("unused")
public class ReactomeEvidence {

    private String releaseDate;

    private String reaction;        //Stable identifier
    private String resource;        //Molecule resource: Uniprot | Ensembl | ChEBI
    private String identifier;      //Molecule identifier
    private List<String> mutations;

    private String doid;
    private String disease;
    private String diseaseResource;
    private String activity;

    private List<PathwayBase> pathways;
    private List<Integer> pubMedIdentifiers;

    public ReactomeEvidence() { }

    public String getReaction() {
        return reaction;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getResource() {
        return resource;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<String> getMutations() {
        return mutations;
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

    public List<Integer> getPubMedIdentifiers() {
        return pubMedIdentifiers;
    }

    public String getDiseaseIdentifier() {
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
        switch (resource.toLowerCase()){
            case "doid":
                System.err.println("DOID " + identifier + " needs to be mapped");
                return "http://purl.obolibrary.org/obo/DOID_" + identifier;
            case "orphanet":
                return "http://www.orpha.net/ORDO/Orphanet_" + identifier;
            case "hp":
                return "http://purl.obolibrary.org/obo/HP_" + identifier;
            case "omim":
                return "http://www.omim.org/entry/" + identifier;
            default:
                return "http://www.ebi.ac.uk/" + resource.toLowerCase() + "/" + resource + "_" + identifier;
        }
    }
}
