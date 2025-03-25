package org.reactome.server.export.tasks.common;

import org.apache.commons.lang3.StringUtils;
import org.reactome.server.graph.service.GeneralService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class DataExportAbstract implements DataExport {

    private GeneralService generalService;

    protected Boolean doTest() {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMap() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean run(GeneralService generalService, String path) {
        this.generalService = generalService;
        if (doTest()) {
            Collection<Map<String, Object>> result = generalService.query(getQuery(), getMap());
            if (result == null || !result.iterator().hasNext()) return false;
            try {
                printResult(result, createFile(path));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public abstract String getQuery();

    public abstract void printResult(Collection<Map<String, Object>> result, Path path) throws IOException;

    protected final void print(Collection<Map<String, Object>> result, Path path, String... attributes) throws IOException {
        print(result, path, true, attributes);
    }

    protected final void print(Collection<Map<String, Object>> result, Path path, boolean header, String... attributes) throws IOException {
        List<String> lines = new ArrayList<>();
        if (header) lines.add(StringUtils.join(attributes, "\t"));
        for (Map<String, Object> map : result) {
            List<String> line = new ArrayList<>();
            boolean lineHasNullValues = false;
            for (String attribute : attributes) {
                // Some results might be list of elements. In some cases we use REDUCE and the output looks like
                // ["a", "b", "c", ] and we want it to look like ["a", "b", "c"].
                //               ^ we remove this comma and the space after it
                // That's why we replace ", ]$" by "]"
                Object aux = map.get(attribute);
                if (aux instanceof Object[]) {
                    StringBuilder rtn = new StringBuilder("[");
                    for (Object item : (Object[]) aux) {
                        rtn.append(item).append(", ");
                    }
                    aux = rtn.append("]").toString();
                }

                lineHasNullValues = (aux == null) || lineHasNullValues;

                line.add((aux == null ? "-" : ("" + aux).replaceAll(", ]$", "]")));

            }
            if (!lineHasNullValues || addNullValues()) {
                lines.add(StringUtils.join(line, "\t"));
            }
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    protected Path createFile(String path) throws IOException {
        String filename = path + getName() + ".txt";
        if (printDbVersion()) filename = filename.replace(".txt", "_v" + generalService.getDBInfo().getVersion() + ".txt");
        Path p = Paths.get(filename);
        Files.deleteIfExists(p);
        if (!Files.isSymbolicLink(p.getParent())) Files.createDirectories(p.getParent());
        Files.createFile(p);
        return p;
    }
}
