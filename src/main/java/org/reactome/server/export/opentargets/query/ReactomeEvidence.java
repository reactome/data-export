package org.reactome.server.export.opentargets.query;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.reactome.server.export.opentargets.mapper.DiseaseMapper;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ReactomeEvidence implements CustomQuery {

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
        return (mutations == null || mutations.isEmpty()) ? null : mutations;
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
        return (pubMedIdentifiers == null || pubMedIdentifiers.isEmpty()) ? null : pubMedIdentifiers;
    }

    public void setReaction(ReactionBase reaction) {
        this.reaction = reaction;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setMutations(List<String> mutations) {
        this.mutations = mutations;
    }

    public void setDoid(String doid) {
        this.doid = doid;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public void setDiseaseResource(String diseaseResource) {
        this.diseaseResource = diseaseResource;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setPathways(List<PathwayBase> pathways) {
        this.pathways = pathways;
    }

    public void setPubMedIdentifiers(List<String> pubMedIdentifiers) {
        this.pubMedIdentifiers = pubMedIdentifiers;
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

    @Override
    public CustomQuery build(Record r) {
        ReactomeEvidence re = new ReactomeEvidence();
        re.setReaction(ReactionBase.build(r.get("reaction")));
        re.setResource(r.get("resource").asString(null));
        re.setIdentifier(r.get("identifier").asString(null));
        re.setDoid(r.get("doid").asString(null));
        re.setDisease(r.get("disease").asString(null));
        re.setDiseaseResource(r.get("diseaseResource").asString(null));
        re.setActivity(r.get("activity").asString(null));
        if (!r.get("mutations").isNull()) re.setMutations(r.get("mutations").asList(Value::asString));
        if (!r.get("pubMedIdentifiers").isNull()) re.setPubMedIdentifiers((r.get("pubMedIdentifiers").asList(Value::asInt).stream().map(String::valueOf).collect(Collectors.toList())));
        if (!r.get("pathways").isNull()) re.setPathways(r.get("pathways").asList(PathwayBase::build));
        return re;
    }
}
