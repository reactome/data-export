package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// JSON parsing imports (Jackson, for example)
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        // 1) Collect valid pathway IDs from the directory
        Set<String> validPathwayIds = getValidPathwayIds();

        // 2) Filter the Neo4j results to include only matching IDs
        List<Map<String, Object>> filteredResults = result.stream()
                .filter(row -> validPathwayIds.contains(row.get("pathwayId")))
                .collect(Collectors.toList());

        // 3) Transform the filtered results by removing "R-HSA-" prefix in a new mutable map
        List<Map<String, Object>> transformedResults = filteredResults.stream().map(unmodifiableRow -> {
            // Create a new HashMap from the unmodifiable row
            Map<String, Object> modifiableRow = new HashMap<>(unmodifiableRow);

            // Remove prefix if needed
            String pId = (String) modifiableRow.get("pathwayId");
            if (pId != null && pId.startsWith("R-HSA-")) {
                modifiableRow.put("pathwayId", pId.substring("R-HSA-".length()));
            }

            return modifiableRow;
        }).collect(Collectors.toList());

        // 4) Print (using parent's print method) the final transformed list
        print(transformedResults, path, "pathwayId", "pathwayName", "isDisease");
    }


    /**
     * Retrieves a set of valid pathway IDs from the JSON filenames in the target directory,
     * excluding those where all nodes have renderableClass="ProcessNode".
     */
    private Set<String> getValidPathwayIds() {
        Set<String> pathwayIds = new HashSet<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DIAGRAM_DIRECTORY))) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry) && entry.toString().endsWith(".json")) {
                    String filename = entry.getFileName().toString();
                    String pathwayId = filename.replace(".json", "");

                    // Exclude this JSON file if it consists only of "ProcessNode" renderableClasses
                    if (!isAllProcessNodes(entry)) {
                        pathwayIds.add(pathwayId);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }

        return pathwayIds;
    }

    /**
     * Checks if the diagram JSON file has ONLY "ProcessNode" items in its "nodes" array.
     * If it does, we return true (meaning it should be excluded).
     */
    private boolean isAllProcessNodes(Path jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonFilePath.toFile());
            JsonNode nodes = root.get("nodes");

            // If nodes is null, not an array, or empty, it's not "all ProcessNode"
            if (nodes == null || !nodes.isArray() || nodes.size() == 0) {
                return false;
            }

            // If ANY node is not "ProcessNode", return false
            for (JsonNode node : nodes) {
                JsonNode renderableClass = node.get("renderableClass");
                if (renderableClass == null || !"ProcessNode".equals(renderableClass.asText())) {
                    return false;
                }
            }
            // Every node was "ProcessNode"
            return true;

        } catch (IOException e) {
            // If JSON can't be parsed, treat it as not exclusively "ProcessNode"
            System.err.println("Error parsing JSON file " + jsonFilePath + ": " + e.getMessage());
            return false;
        }
    }
}
