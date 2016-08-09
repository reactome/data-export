package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Florian Korninger <florian.korninger@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class CountEntriesForStatistics extends DataExportAbstract {

    @Override
    public String getName() { return "CountEntriesForStatistics"; }

    @Override
    public String getQuery() {
        return " MATCH (n:Pathway) RETURN \"pathway\" AS Type, count(n) as Count\n" +
                "UNION MATCH (n:ReactionLikeEvent) RETURN \"reaction\" AS Type, count(n) as Count\n" +
                "UNION MATCH (n:Complex) RETURN \"complex\" AS Type, count(n) as Count\n" +
                "UNION MATCH (n:EntitySet) RETURN \"set\" AS Type, count(n) as Count\n" +
                "UNION MATCH (n:Polymer) RETURN \"polymer\" AS Type, count(n) as Count\n" +
                "UNION MATCH (n:SimpleEntity) RETURN \"chemical\" AS Type, count(n) as Count\n" +
                "UNION MATCH (ewas:EntityWithAccessionedSequence)-[:referenceEntity]-(n:ReferenceGeneProduct) RETURN \"protein\" AS Type, count(n) as Count\n" +
                "UNION MATCH (ewas:EntityWithAccessionedSequence)-[:referenceEntity]-(n:ReferenceDNASequence) RETURN \"dna\" AS Type, count(n) as Count\n" +
                "UNION MATCH (ewas:EntityWithAccessionedSequence)-[:referenceEntity]-(n:ReferenceRNASequence) RETURN \"rna\" AS Type, count(n) as Count\n";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "Type", "Count");
    }
}
