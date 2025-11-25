package org.reactome.server.export.gmt;

import org.reactome.server.export.gmt.model.GmtPathway;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Exports the pathway information in GTM format. It uses {@link GmtPathway} to store the result of the query.
 * The GMT format is a tabular format where the columns are:
 *   First  -> Pathway Name
 *   Second -> URI (identifier)
 *   Other  -> Contained identifiers (based on resource) - separated by tab
 */
public class GmtExporter {

    private static final Logger logger = LoggerFactory.getLogger("gmt-exporter");

    /**
     * Resources for which a GMT file is provided
     */
    @SuppressWarnings("unused")
    private enum Resources {
        UNIPROT("UniProt"),
        CHEBI("ChEBI"),
        ENSEMBL("Ensembl", "(ENSEMBL|Ensembl)"),
        NCBI("NCBI", "NCBI.*"),
        MIRBASE("miRBase"),
        GtoP("GtoP","(IUPHAR|Guide to Pharmacology|Guide to Pharmacology - Ligands|GtoP)");

        private final String name;
        private final String query;

        Resources(String name) {
            this.name = this.query = name;
        }

        Resources(String name, String query) {
            this.name = name;
            this.query = query;
        }
    }

    private static final String QUERY_IDS = "" +
            "MATCH (rd:ReferenceDatabase) " +
            "WHERE rd.displayName =~ $referenceDatabase " +
            "WITH COLLECT(DISTINCT rd) AS rds " +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(:ReactionLikeEvent)-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]->(pe:PhysicalEntity) " +
            "WITH DISTINCT rds, p, COLLECT(DISTINCT pe) AS pes " +
            "UNWIND pes AS x " +
            "MATCH (x)-[:referenceEntity|referenceSequence|referenceGene|crossReference*]->(n) " +
            "WITH DISTINCT rds, p, COLLECT(DISTINCT n) AS ns " +
            "UNWIND ns AS n " +
            "MATCH (n)-[:referenceDatabase]->(rd) WHERE rd IN rds " +
            "WITH DISTINCT p, COLLECT(DISTINCT CASE WHEN n.variantIdentifier IS NULL THEN n.identifier ELSE n.variantIdentifier END) AS identifiers " +
            "RETURN p.stId AS stId, p.displayName AS name, identifiers " +
            "ORDER BY stId";

    private static final String QUERY_GENE_NAME = "" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(:ReactionLikeEvent)-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]->(pe:PhysicalEntity) " +
            "WITH DISTINCT p, COLLECT(DISTINCT pe) AS pes " +
            "UNWIND pes AS pe " +
            "MATCH (pe)-[:referenceEntity]->(re:ReferenceEntity) " +
            "WITH DISTINCT p, COLLECT(DISTINCT HEAD(re.geneName)) AS identifiers " +
            "WHERE SIZE(identifiers) > 0 " +
            "RETURN p.stId AS stId, p.displayName AS name, identifiers " +
            "ORDER BY stId";

    public static void export(AdvancedDatabaseObjectService service, String path, boolean verbose) {
        long start = System.currentTimeMillis();
        exportIdentifiers(service, path, verbose);
        exportGeneNames(service, path, verbose);
        String time = getTimeFormatted(System.currentTimeMillis() - start);
        logger.info("GMT exporter finished in {}", time);
        if (verbose) System.out.printf("\rGMT exporter >> Done [%s]%n", time);
    }

    private static void exportIdentifiers(AdvancedDatabaseObjectService service, String path, boolean verbose) {
        int i = 0, total = Resources.values().length;
        for (Resources resource : Resources.values()) {
            try {
                if (verbose) System.out.printf("\rRunning GMT file exporter for '%s' [%d/%d]", resource.name, ++i, total);

                long partial_start = System.currentTimeMillis();
                Map<String, Object> params = new HashMap<>();
                params.put("referenceDatabase", resource.query);
                Collection<GmtPathway> pathways = service.getCustomQueryResults(GmtPathway.class, QUERY_IDS, params);

                String fileName = path + "Reactome_" + resource.name + ".gmt";
                saveGmtFile(fileName, pathways);

                String partial_time = getTimeFormatted(System.currentTimeMillis() - partial_start);
                logger.info(String.format("GMT file for '%s' generated in %s", resource.name, partial_time));
            } catch (CustomQueryException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\rRunning GMT file exporter for 'identifiers' >> Done");
    }

    private static void exportGeneNames(AdvancedDatabaseObjectService service, String path, boolean verbose) {
        try {
            if (verbose) System.out.print("Running GMT file exporter for 'gene names'");

            long partial_start = System.currentTimeMillis();
            Collection<GmtPathway> pathways = service.getCustomQueryResults(GmtPathway.class, QUERY_GENE_NAME);

            String fileName = path + "Reactome_GeneName.gmt";
            saveGmtFile(fileName, pathways);

            String partial_time = getTimeFormatted(System.currentTimeMillis() - partial_start);
            if (verbose) System.out.println("\rRunning GMT file exporter for 'gene names' >> Done");
            logger.info(String.format("GMT file for 'gene names' generated in %s", partial_time));
        } catch (CustomQueryException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void saveGmtFile(String fileName, Collection<GmtPathway> pathways) throws FileNotFoundException {
        PrintStream ps = new PrintStream(new FileOutputStream(new File(fileName)));
        for (GmtPathway pathway : pathways) ps.println(pathway.getTabularFormat());
        ps.close();
    }

    private static String getTimeFormatted(Long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
