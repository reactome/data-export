package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class Complex_2_Pathway_Human extends DataExportAbstract {

    @Override
    public String getName() {
        return "Complex_2_Pathway_human";
    }

    @Override
    public String getQuery() {
        return " MATCH (c:Complex)<-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway{speciesName:\"Homo sapiens\"})" +
                "WITH c, p " +
                "MATCH (p)<-[:hasEvent*]-(tlp:TopLevelPathway) " +
                "RETURN DISTINCT c.stId AS complex, p.stId AS pathway, tlp.stId AS top_level_pathway " +
                "ORDER BY complex, pathway, top_level_pathway";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "complex", "pathway", "top_level_pathway");
    }
}
