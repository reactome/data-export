package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * @author Joel Weiser <joel.weiser@oicr.on.ca>
 * @author Adam Wright <adam.wright@oicr.on.ca>
 */
@SuppressWarnings("unused")
@DataExport
public class PathwaysGoTerms extends DataExportAbstract {

    @Override
    public String getName() {
        return "Pathways2GoTerms_human";
    }

    @Override
    public String getQuery() {
        return "MATCH (n:Pathway {speciesName: \"Homo sapiens\"})-[:goBiologicalProcess]->(go_term:GO_BiologicalProcess)\n" +
            "OPTIONAL MATCH (n)<-[:hasEvent]-(ancestor:Pathway)-[:goBiologicalProcess]->(ancestor_go_term:GO_BiologicalProcess)\n" +
            "WITH n, go_term, COUNT(ancestor) AS ancestor_count, COLLECT(ancestor_go_term.accession) AS ancestor_accessions\n" +
            "WHERE ancestor_count = 0 OR NOT go_term.accession IN ancestor_accessions\n" +
            "RETURN\n" +
            "n.stId AS Identifier,\n" +
            "n.displayName AS Name,\n" +
            "\"GO:\" + go_term.accession AS GO_Term\n" +
            "ORDER BY Name\n";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "Identifier", "Name", "GO_Term");
    }
}
