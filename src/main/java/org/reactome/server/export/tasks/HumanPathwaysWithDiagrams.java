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
        return "MATCH (p:Pathway)-[:species]->(:Species {dbId: 48887})\n"+
        "OPTIONAL MATCH (p)-[:disease]->(d:Disease)\n"+
        "WITH p, d IS NOT NULL AS isDisease\n"+
        "RETURN p.stId AS pathwayId, p.displayName AS pathwayName, isDisease\n";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "pathwayId", "pathwayName", "isDisease");
    }

    // check if st ids are in the diagram
}
