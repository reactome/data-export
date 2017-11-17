package org.reactome.server.export.mapping;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.model.Result;
import org.reactome.server.graph.service.GeneralService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Mapping {

    private enum Resources {
        UNIPROT("UniProt"),
        CHEBI("ChEBI"),
        ENSEMBL("Ensembl", "(ENSEMBL|Ensembl)"),
        NCBI("NCBI", "NCBI.*"),
        MIRBASE("miRBase");

        private String name;
        private String query;

        Resources(String name) {
            this.name = this.query = name;
        }

        Resources(String name, String query) {
            this.name = name;
            this.query = query;
        }
    }

    private enum ExportType {
        LOWER_LEVEL_PATHWAY("LLP"),
        ALL_PATHWAYS("all pathways"),
        REACTIONS("reactions");

        private String name;

        ExportType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum ExportPhysicalEntity {
        INCLUDE(true), EXCLUDE(false);

        private boolean toPE;

        ExportPhysicalEntity(boolean toPE) {
            this.toPE = toPE;
        }
    }

    @SuppressWarnings("unchecked")
    public static void run(GeneralService generalService, String path, boolean verbose) {
        int size = Resources.values().length * 6;
        int current = 0; int count = 0; Long time = 0L;
        for (Resources resource : Resources.values()) {
            for (ExportPhysicalEntity exportPhysicalEntity : ExportPhysicalEntity.values()) {
                for (ExportType type : ExportType.values()) {
                    Result result = null;
                    List<String> attr = null;
                    String fileName = resource.name + "2Reactome";
                    Long start = System.currentTimeMillis();
                    if (verbose) {
                        if (time > 0) System.out.println(" (" + getTimeFormatted(time) + ")");
                        System.out.print("\rRunning " + resource.name + " mapping for " + type + (exportPhysicalEntity.toPE ? " including PEs" : "") + " [" + ++current + " of " + size + "]");
                    }
                    switch (type) {
                        case LOWER_LEVEL_PATHWAY:
                            if (exportPhysicalEntity.toPE) {
                                result = generalService.query(MappingQueries.queryResourceToPEAndLLP(resource.query), Collections.EMPTY_MAP);
                                attr = Arrays.asList("Identifier", "Entity_ID", "Entity_Name", "Pathway_ID", "Link", "Pathway_Name", "Evidence_Code", "Species");
                                fileName += "_PE_Pathway";
                            } else {
                                result = generalService.query(MappingQueries.queryResourceToLLP(resource.query), Collections.EMPTY_MAP);
                                attr = Arrays.asList("Identifier", "Pathway_ID", "Link", "Pathway_Name", "Evidence_Code", "Species");
                            }
                            break;
                        case ALL_PATHWAYS:
                            if (exportPhysicalEntity.toPE) {
                                result = generalService.query(MappingQueries.queryResourceToPEAndAllPathways(resource.query), Collections.EMPTY_MAP);
                                attr = Arrays.asList("Identifier", "Entity_ID", "Entity_Name", "Pathway_ID", "Link", "Pathway_Name", "Evidence_Code", "Species");
                                fileName += "_PE_All_Levels";
                            } else {
                                result = generalService.query(MappingQueries.queryResourceToAllPathways(resource.query), Collections.EMPTY_MAP);
                                attr = Arrays.asList("Identifier", "Pathway_ID", "Link", "Pathway_Name", "Evidence_Code", "Species");
                                fileName += "_All_Levels";
                            }
                            break;
                        case REACTIONS:
                            if (exportPhysicalEntity.toPE) {
                                result = generalService.query(MappingQueries.queryResourceToPEAndReactions(resource.query), Collections.EMPTY_MAP);
                                attr = Arrays.asList("Identifier", "Entity_ID", "Entity_Name", "Reaction_ID", "Link", "Reaction_Name", "Evidence_Code", "Species");
                                fileName += "_PE_Reactions";
                            } else {
                                result = generalService.query(MappingQueries.queryResourceToReactions(resource.query), Collections.EMPTY_MAP);
                                attr = Arrays.asList("Identifier", "Reaction_ID", "Link", "Reaction_Name", "Evidence_Code", "Species");
                                fileName += "Reactions";
                            }
                            break;
                    }
                    if (result != null && result.iterator().hasNext()) {
                        try {
                            Path p = createFile(path, fileName);
                            printMapping(result, p, attr);
                            count++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    time = System.currentTimeMillis() - start;
                }
            }
            //Only one cleaning after each resource
            generalService.clearCache();
        }
        if(verbose) System.out.println("\rMapping finished. " + count + " files has been generated.");
    }

    private static void printMapping(Result result, Path path, List<String> attributes) throws IOException {
        List<String> lines = new ArrayList<>();
        attributes = new ArrayList<>(attributes);
        for (Map<String, Object> map : result) {
            List<String> line = new ArrayList<>();
            for (String attribute : attributes) {
                if (attribute.equals("Identifier")) {
                    String id = (String) map.get("variant" + attribute);
                    line.add(id != null ? id : (String) map.get(attribute));
                } else if (attribute.equals("Link")) {
                    String link = (String) map.get("Pathway_ID");
                    link = link != null ? link : (String) map.get("Reaction_ID");
                    line.add("https://reactome.org/PathwayBrowser/#/" + link);
                } else if (attribute.equals("Evidence_Code")) {
                    line.add(((Boolean) map.get(attribute)) ? "IEA" : "TAS");
                } else {
                    Object aux = map.get(attribute);
                    // Some results might be list of elements. In this case we use UNWIND and the output looks like
                    // ["a", "b", "c", ] and we want it to look like ["a", "b", "c"].
                    //               ^ we remove this comma and the space after it
                    // That's why we replace ", ]" by "]"
                    line.add(aux == null ? "-" : ("" + aux).replaceAll(", ]$", "]"));
                }
            }
            lines.add(StringUtils.join(line, "\t"));
        }
        Files.write(path, lines, Charset.forName("UTF-8"));
    }

    private static Path createFile(String path, String fileName) throws IOException {
        Path p = Paths.get(path + fileName + ".txt");
        Files.deleteIfExists(p);
        if(!Files.isSymbolicLink(p.getParent())) Files.createDirectories(p.getParent());
        Files.createFile(p);
        return p;
    }

    private static String getTimeFormatted(Long millis){
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
