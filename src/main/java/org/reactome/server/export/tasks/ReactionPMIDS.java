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
public class ReactionPMIDS extends DataExportAbstract {


    @Override
    public String getName() {
        return "ReactionPMIDS";
    }

    @Override
    public String getQuery() {
        return " MATCH (rle:ReactionLikeEvent)-[:literatureReference]->(lr:LiteratureReference) " +
                "RETURN DISTINCT rle.stId AS Reaction, lr.pubMedIdentifier AS PMID";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, false, "Reaction", "PMID");
    }

}
