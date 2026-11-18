package org.reactome.server.export.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StableIdsToOldIdsTest {

    private static Map<String, Object> row(long dbId, String stId, String oldStId) {
        Map<String, Object> row = new HashMap<>();
        row.put("release", 98L);
        row.put("dbId", dbId);
        row.put("stId", stId);
        row.put("oldStId", oldStId);
        return row;
    }

    private static final List<Map<String, Object>> ROWS = Arrays.asList(
            row(1006169, "R-BTA-1006169", "REACT_272094"),
            row(893583, "R-HSA-893583", "REACT_3"),
            row(8877760, "R-HSA-8877760", "REACT_2"),
            row(109266, "R-HSA-109266", "REACT_1"),
            row(48887, "R-ALL-48887", "REACT_4"));

    @Test
    void headerThenHumanThenOtherSpeciesEachByDbIdAsString() {
        assertEquals(Arrays.asList(
                "# Reactome stable IDs for release 98",
                "Stable_ID\told_identifier(s)",
                "R-HSA-109266\tREACT_1",
                "R-HSA-8877760\tREACT_2",
                "R-HSA-893583\tREACT_3",
                "R-BTA-1006169\tREACT_272094",
                "R-ALL-48887\tREACT_4"), StableIdsToOldIds.toLines(ROWS));
    }

    @Test
    void writesTheFileTheReleasePublishes(@TempDir Path dir) throws IOException {
        StableIdsToOldIds task = new StableIdsToOldIds();
        assertEquals("reactome_stable_ids", task.getName());
        Path file = dir.resolve(task.getName() + ".txt");
        task.printResult(ROWS.subList(0, 2), file);
        assertEquals("# Reactome stable IDs for release 98\nStable_ID\told_identifier(s)\nR-HSA-893583\tREACT_3\nR-BTA-1006169\tREACT_272094\n",
                new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
    }
}
