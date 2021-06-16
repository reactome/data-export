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
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, false, "Reaction", "PMID");
    }

}
