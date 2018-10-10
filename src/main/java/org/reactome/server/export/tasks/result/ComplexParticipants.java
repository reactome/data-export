package org.reactome.server.export.tasks.result;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComplexParticipants {

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
}
