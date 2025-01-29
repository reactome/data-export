package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Export Pathway Summation Mapping from Neo4j
 */
@DataExport
public class HumanPathwaysWithDiagrams extends DataExportAbstract {

    @Override
    public String getName() {
        return "humanPathwaysWithDiagrams";
    }

    @Override
    public String getQuery() {
        return "MATCH (p:Pathway {speciesName: 'Homo sapiens'})\n" +
                "OPTIONAL MATCH (p)-[:summation]->(s:Summation)\n" +
                "RETURN\n" +
                "  p.stId AS Identifier,\n" +
                "  p.displayName AS Name,\n" +
                "  COALESCE(s.text, '') AS Summation\n" +
                "ORDER BY Name";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "Identifier", "Name", "Summation");
    }
}
