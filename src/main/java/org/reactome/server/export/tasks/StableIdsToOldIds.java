package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Exports reactome_stable_ids.txt: every current stable identifier that has an old (pre release 53, REACT_...)
 * identifier, and that old identifier. The release published this file from the relational stable-identifier
 * database (release-download-directory's MapOldStableIds); this task makes it from the graph alone.
 *
 * <pre>
 * # Reactome stable IDs for release {release}
 * Stable_ID \t old_identifier(s)
 * R-HSA-1006143 \t REACT_118575
 * </pre>
 *
 * The graph keeps one old identifier per object (DatabaseObject.oldStId), so each line has exactly one. The
 * lines are in the order the relational step used: human identifiers first, then the other species, each by
 * the object's dbId compared as a string.
 */
@DataExport
public class StableIdsToOldIds extends DataExportAbstract {

    @Override
    public String getName() {
        return "reactome_stable_ids";
    }

    @Override
    public String getQuery() {
        return "MATCH (d:DBInfo) WITH d.releaseNumber AS release " +
               "MATCH (n:DatabaseObject) WHERE n.stId IS NOT NULL AND n.oldStId IS NOT NULL " +
               "RETURN release, n.dbId AS dbId, n.stId AS stId, n.oldStId AS oldStId";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        Files.write(path, (String.join("\n", toLines(result)) + "\n").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Turns the query's rows (release, dbId, stId, oldStId) into the file's lines, header included.
     */
    static List<String> toLines(Collection<Map<String, Object>> result) {
        List<Mapping> mappings = new ArrayList<>();
        Object release = null;
        for (Map<String, Object> row : result) {
            release = row.get("release");
            mappings.add(new Mapping(row));
        }
        mappings.sort(Comparator.comparing((Mapping m) -> !m.stId.startsWith("R-HSA-")).thenComparing(m -> m.dbId));

        List<String> lines = new ArrayList<>();
        lines.add("# Reactome stable IDs for release " + release);
        lines.add("Stable_ID\told_identifier(s)");
        for (Mapping mapping : mappings) lines.add(mapping.stId + "\t" + mapping.oldStId);
        return lines;
    }

    private static final class Mapping {
        private final String dbId;
        private final String stId;
        private final String oldStId;

        Mapping(Map<String, Object> row) {
            this.dbId = String.valueOf(row.get("dbId"));
            this.stId = (String) row.get("stId");
            this.oldStId = (String) row.get("oldStId");
        }
    }
}
