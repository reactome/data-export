package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Export Pathway Summation Mapping from Neo4j
 */
@DataExport
public class HumanPathwaysWithDiagrams extends DataExportAbstract {

    private static final String DIAGRAM_DIRECTORY = "/data/diagrams";

    @Override
    public String getName() {
        return "humanPathwaysWithDiagrams";
    }

    @Override
    public String getQuery() {
        return "MATCH (p:Pathway)-[:species]->(:Species {dbId: 48887})\n" +
               "OPTIONAL MATCH (p)-[:disease]->(d:Disease)\n" +
               "WITH p, d IS NOT NULL AS isDisease\n" +
               "RETURN DISTINCT p.stId AS pathwayId, p.displayName AS pathwayName, isDisease\n";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        // Get valid pathway IDs from JSON files in the directory
        Set<String> validPathwayIds = getValidPathwayIds();

        // Filter results to include only those with matching pathway IDs
        List<Map<String, Object>> filteredResults = result.stream()
                .filter(map -> validPathwayIds.contains(map.get("pathwayId")))
                .collect(Collectors.toList());

        // Print only the filtered results
        print(filteredResults, path, "pathwayId", "pathwayName", "isDisease");
    }

    /**
     * Retrieves a set of valid pathway IDs from the JSON filenames in the target directory.
     */
    private Set<String> getValidPathwayIds() {
        Set<String> pathwayIds = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DIAGRAM_DIRECTORY), "*.json")) {
            for (Path entry : stream) {
                String filename = entry.getFileName().toString();
                String pathwayId = filename.replace(".json", ""); // Remove .json extension
                pathwayIds.add(pathwayId);
            }
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }
        return pathwayIds;
    }
}
