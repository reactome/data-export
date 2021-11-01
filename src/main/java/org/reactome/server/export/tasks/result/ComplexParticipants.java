package org.reactome.server.export.tasks.result;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComplexParticipants implements CustomQuery {

    private String identifier;
    private String name;
    private List<Participant> participants;
    private List<String> participatingComplexes;
    private Set<Integer> pubMedIdentifiers = new HashSet<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<String> getParticipatingComplexes() {
        return participatingComplexes;
    }

    public void setParticipatingComplexes(List<String> participatingComplexes) {
        this.participatingComplexes = participatingComplexes;
    }

    public void setPubMedIdentifiers(Set<Integer> pubMedIdentifiers) {
        this.pubMedIdentifiers = pubMedIdentifiers;
    }

    public Set<Integer> getPubMedIdentifiers() {
        return pubMedIdentifiers;
    }

    public void setPubMedIdentifiers(List<Integer> pubMedIdentifiers) {
        this.pubMedIdentifiers.addAll(pubMedIdentifiers);
    }

    private String toString(Collection<?> collection) {
        return collection.isEmpty() ? "-" : StringUtils.join(collection, "|");
    }

    @Override
    public String toString() {
        return String.format("%s\t\"%s\"\t%s\t%s\t%s",
                identifier,
                name,
                toString(participants),
                toString(participatingComplexes),
                toString(pubMedIdentifiers)
        );
    }

    @Override
    public CustomQuery build(Record r) {
        ComplexParticipants cp = new ComplexParticipants();
        cp.setIdentifier(r.get("identifier").asString(null));
        cp.setName(r.get("name").asString(null));
        cp.setParticipatingComplexes(r.get("participatingComplexes").asList(Value::asString));
        cp.setPubMedIdentifiers(r.get("pubMedIdentifiers").asList(Value::asInt));
        cp.setParticipants(r.get("participants").asList(Participant::build));
        return cp;
    }
}
