package org.reactome.server.export.sequence.model;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class SequenceReaction {

    private String pathway;
    private String reaction;
    private String reactionName;
    private String uniprotId;
    private List<String> rolesInReaction;

    public String getPathway() {
        return pathway;
    }

    public void setPathway(String pathway) {
        this.pathway = pathway;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public void setReactionName(String reactionName) {
        this.reactionName = reactionName;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    public void setRolesInReaction(List<String> rolesInReaction) {
        this.rolesInReaction = rolesInReaction;
    }

    public String getTabularFormat() {
        return String.format("%s\t%s\t%s\t%s\t%s", pathway, reaction, reactionName, uniprotId, rolesInReaction != null ? "\"" + String.join(",", rolesInReaction) + "\"" : "");
    }

    public static String getHeader() {
        return "pathway_id\treaction_id\treaction_name\tuniprot_acc\trole_in_reaction";
    }
}
