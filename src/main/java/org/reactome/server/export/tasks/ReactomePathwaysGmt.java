package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exports the human pathways' gene sets as ReactomePathways.gmt, zipped as ReactomePathways.gmt.zip:
 * the GSEA file the release publishes in its download directory.
 *
 * Each line is the pathway's name, its stable identifier and the gene names of its participants, tab separated:
 * <pre>name \t stId \t gene \t gene ...</pre>
 *
 * The file follows the rules of the exporter that made it from the relational database
 * (Pathway-Exchange's ReactomeToMsigDBExport), so that this one can replace it:
 * <ul>
 *   <li>pathways are those whose species include Homo sapiens;</li>
 *   <li>a pathway's participants are the inputs, outputs, catalysts and physical-entity regulators of every
 *       reaction-like event below it; complexes, sets and polymers are followed down to their reference entities;
 *       cell markers are not followed;</li>
 *   <li>a gene is the first gene name of a ReferenceSequence; a pathway with no gene is left out;</li>
 *   <li>names are trimmed; names shared by several pathways get "_" and the pathway's dbId appended, so that
 *       every name is unique (GSEA tools need that). Names are compared trimmed: the relational exporter
 *       compared them before trimming, so two that differed only by a trailing space came out the same
 *       (none did in release 98's file);</li>
 *   <li>lines are ordered by name, then dbId, and genes by name (both Java's String order).</li>
 * </ul>
 */
@DataExport
public class ReactomePathwaysGmt extends DataExportAbstract {

    static final String FILE_NAME = "ReactomePathways.gmt";

    @Override
    public String getName() {
        return "ReactomePathwaysGmt";
    }

    @Override
    public String getQuery() {
        return "MATCH (p:Pathway)-[:species]->(:Species{dbId: 48887}) " +
               "MATCH (p)-[:hasEvent*]->(rle:ReactionLikeEvent) " +
               "WITH DISTINCT p, rle " +
               "CALL { " +
               "  WITH rle MATCH (rle)-[:input|output]->(pe:PhysicalEntity) RETURN pe " +
               "  UNION " +
               "  WITH rle MATCH (rle)-[:catalystActivity]->(:CatalystActivity)-[:physicalEntity]->(pe:PhysicalEntity) RETURN pe " +
               "  UNION " +
               "  WITH rle MATCH (rle)-[:regulatedBy]->(:Regulation)-[:regulator]->(pe:PhysicalEntity) RETURN pe " +
               "} " +
               "WITH p, COLLECT(DISTINCT pe) AS pes " +
               "UNWIND pes AS pe " +
               "MATCH (pe)-[:hasComponent|hasMember|hasCandidate|repeatedUnit*0..]->(:PhysicalEntity)-[:referenceEntity]->(re:ReferenceSequence) " +
               "WITH p, COLLECT(DISTINCT re.geneName[0]) AS genes " +
               "RETURN p.dbId AS dbId, p.stId AS stId, p.displayName AS name, genes";
    }

    @Override
    protected Path createFile(String path) throws IOException {
        Path p = Paths.get(path + FILE_NAME + ".zip");
        Files.deleteIfExists(p);
        if (!Files.isSymbolicLink(p.getParent())) Files.createDirectories(p.getParent());
        return p;
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        try (OutputStream os = Files.newOutputStream(path); ZipOutputStream zos = new ZipOutputStream(os)) {
            zos.putNextEntry(new ZipEntry(FILE_NAME));
            for (String line : toLines(result)) {
                zos.write((line + "\n").getBytes(StandardCharsets.UTF_8));
            }
            zos.closeEntry();
        }
    }

    /**
     * Turns the query's rows (dbId, stId, name, genes) into the file's lines, without line ends.
     */
    static List<String> toLines(Collection<Map<String, Object>> result) {
        List<GeneSet> sets = new ArrayList<>();
        Map<String, Integer> nameCount = new HashMap<>();
        for (Map<String, Object> row : result) {
            GeneSet set = new GeneSet(row);
            sets.add(set);
            nameCount.merge(set.name, 1, Integer::sum);
        }
        for (GeneSet set : sets) {
            if (nameCount.get(set.name) > 1) set.name = set.name + "_" + set.dbId;
        }
        sets.sort(Comparator.comparing((GeneSet s) -> s.name).thenComparing(s -> s.dbId));

        List<String> lines = new ArrayList<>();
        Set<String> written = new HashSet<>();
        for (GeneSet set : sets) {
            if (set.genes.isEmpty() || !written.add(set.name)) continue;
            lines.add(set.name + "\t" + set.stId + "\t" + String.join("\t", set.genes));
        }
        return lines;
    }

    private static final class GeneSet {
        private final long dbId;
        private final String stId;
        private String name;
        private final TreeSet<String> genes = new TreeSet<>();

        GeneSet(Map<String, Object> row) {
            this.dbId = ((Number) row.get("dbId")).longValue();
            this.stId = (String) row.get("stId");
            this.name = ((String) row.get("name")).trim();
            Object genes = row.get("genes");
            if (genes instanceof Collection) {
                for (Object gene : (Collection<?>) genes) {
                    if (gene != null) this.genes.add(gene.toString());
                }
            }
        }
    }
}
