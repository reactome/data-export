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

    @SuppressWarnings("unchecked")
    public static void run(GeneralService genericService, String path, boolean verbose) {
        int size = Resources.values().length * 3;
        int current = 0; int count = 0;
        for (Resources resource : Resources.values()) {
            for (String type : Arrays.asList("LLP", "all pathways", "reactions")) {
                Result result = null;
                List<String> attr = null;
                String fileName = resource.name + "2Reactome";
                if(verbose) System.out.print("\rRunning " + resource.name + " mapping for " + type + " [" + ++current + " of " + size + "]");
                switch (type) {
                    case "LLP":
                        result = genericService.query(MappingQueries.queryResourceToLLP(resource.query), Collections.EMPTY_MAP);
                        attr = Arrays.asList("Identifier", "Pathway_ID", "Pathway_Name", "Evidence_Code", "Species");
                        break;
                    case "all pathways":
                        result = genericService.query(MappingQueries.queryResourceToAllPathways(resource.query), Collections.EMPTY_MAP);
                        attr = Arrays.asList("Identifier", "Pathway_ID", "Pathway_Name", "Evidence_Code", "Species");
                        fileName += "_All_Levels";
                        break;
                    case "reactions":
                        result = genericService.query(MappingQueries.queryResourceToReactions(resource.query), Collections.EMPTY_MAP);
                        attr = Arrays.asList("Identifier", "Reaction_ID", "Reaction_Name", "Evidence_Code", "Species");
                        fileName += "Reactions";
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
            }
        }
        if(verbose) System.out.println("\rMapping finished. " + count + " files has been generated.");
    }

    private static void printMapping(Result result, Path path, List<String> attributes) throws IOException {
        List<String> lines = new ArrayList<>();
        attributes = new ArrayList<>(attributes);
        attributes.add(1, "Link");
        for (Map<String, Object> map : result) {
            List<String> line = new ArrayList<>();
            for (String attribute : attributes) {
                if (attribute.equals("Identifier")) {
                    String id = (String) map.get("variant" + attribute);
                    line.add(id != null ? id : (String) map.get(attribute));
                } else if (attribute.equals("Evidence_Code")) {
                    line.add(((Boolean) map.get(attribute)) ? "IEA" : "TAS");
                } else if (attribute.equals("Link")) {
                    String link = (String) map.get("Pathway_ID");
                    link = link != null ? link : (String) map.get("Reaction_ID");
                    line.add("http://reactome.org/PathwayBrowser/#/" + link);
                } else {
                    Object aux = map.get(attribute);
                    line.add(aux == null ? "-" : "" + aux);
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
}
