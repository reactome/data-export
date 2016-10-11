package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class EWAS_2_Pathway_Human extends DataExportAbstract {

    @Override
    public String getName() {
        return "Ewas2Pathway_human";
    }

    @Override
    public String getQuery() {
        return " MATCH (ewas:EntityWithAccessionedSequence)<-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit*]-(rle:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway{speciesName:\"Homo sapiens\"})" +
                "WITH ewas, p " +
                "MATCH (p)<-[:hasEvent*]-(tlp:TopLevelPathway) " +
                "RETURN DISTINCT ewas.stId AS ewas, p.stId AS pathway, tlp.stId AS top_level_pathway " +
                "ORDER BY ewas, pathway, top_level_pathway";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "ewas", "pathway", "top_level_pathway");
    }
}
