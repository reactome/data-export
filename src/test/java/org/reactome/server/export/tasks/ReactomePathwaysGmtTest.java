package org.reactome.server.export.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReactomePathwaysGmtTest {

    private static Map<String, Object> row(long dbId, String name, String... genes) {
        Map<String, Object> row = new HashMap<>();
        row.put("dbId", dbId);
        row.put("stId", "R-HSA-" + dbId);
        row.put("name", name);
        row.put("genes", Arrays.asList(genes));
        return row;
    }

    @Test
    void linesAreNameThenStIdThenSortedGenes() {
        List<String> lines = ReactomePathwaysGmt.toLines(Collections.singletonList(row(1, "Apoptosis", "TP53", "BAX", "AKT1")));
        assertEquals(Collections.singletonList("Apoptosis\tR-HSA-1\tAKT1\tBAX\tTP53"), lines);
    }

    @Test
    void repeatedNamesGetTheirDbIdAndLinesAreSortedByName() {
        Collection<Map<String, Object>> rows = Arrays.asList(
                row(30, "b pathway", "G1"),
                row(20, "Attachment and Entry", "G2"),
                row(10, "Attachment and Entry", "G3"),
                row(40, "3-hydroxy", "G4"),
                row(50, "3-Methyl", "G5"));
        List<String> lines = ReactomePathwaysGmt.toLines(rows);
        assertEquals(Arrays.asList(
                "3-Methyl\tR-HSA-50\tG5",
                "3-hydroxy\tR-HSA-40\tG4",
                "Attachment and Entry_10\tR-HSA-10\tG3",
                "Attachment and Entry_20\tR-HSA-20\tG2",
                "b pathway\tR-HSA-30\tG1"), lines);
    }

    @Test
    void namesThatDifferOnlyByTrailingSpaceAreMadeUnique() {
        // compared before trimming, both were written "Signalling": two gene sets of one name
        Collection<Map<String, Object>> rows = Arrays.asList(
                row(1, "Signalling ", "A"),
                row(2, "Signalling", "B"));
        List<String> lines = ReactomePathwaysGmt.toLines(rows);
        assertEquals(Arrays.asList("Signalling_1\tR-HSA-1\tA", "Signalling_2\tR-HSA-2\tB"), lines);
    }

    @Test
    void pathwaysWithoutGenesAreLeftOutAndNullGenesIgnored() {
        Collection<Map<String, Object>> rows = Arrays.asList(
                row(1, "Empty"),
                row(2, "Some", "X", null, "X"));
        assertEquals(Collections.singletonList("Some\tR-HSA-2\tX"), ReactomePathwaysGmt.toLines(rows));
    }

    @Test
    void writesOneGmtEntryInTheZip(@TempDir Path dir) throws IOException {
        ReactomePathwaysGmt task = new ReactomePathwaysGmt();
        Path zip = task.createFile(dir.toString() + "/");
        assertEquals("ReactomePathways.gmt.zip", zip.getFileName().toString());
        task.printResult(Arrays.asList(row(2, "B é", "Y"), row(1, "A", "X")), zip);
        try (InputStream is = Files.newInputStream(zip); ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry = zis.getNextEntry();
            assertEquals("ReactomePathways.gmt", entry.getName());
            String content = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals("A\tR-HSA-1\tX\nB é\tR-HSA-2\tY\n", content);
            assertNull(zis.getNextEntry());
        }
    }
}
