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
public class IntAct_Static extends DataExportAbstract {

    @Override
    public String getName() {
        return "IntAct_Static";
    }

    @Override
    public String getQuery() {
        return " MATCH (:PhysicalEntity)-[:referenceEntity]->(a:ReferenceEntity)<-[:interactor]-(interaction:Interaction)-[:interactor]->(b:ReferenceEntity) " +
                "WITH CASE WHEN NOT a.variantIdentifier IS NULL THEN a.variantIdentifier ELSE a.identifier END AS a, " +
                "     CASE WHEN NOT b.variantIdentifier IS NULL THEN b.variantIdentifier ELSE b.identifier END AS b, " +
                "     interaction " +
                "RETURN DISTINCT a AS A, b AS B, interaction.score AS Score, interaction.accession AS Accession";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "A", "B", "Score", "Accession");
    }
}
