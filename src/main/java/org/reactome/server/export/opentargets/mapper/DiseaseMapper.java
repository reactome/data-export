package org.reactome.server.export.opentargets.mapper;

import org.reactome.server.export.opentargets.query.ReactomeEvidence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiseaseMapper {

    public static final DiseaseMapper doidMapper;

    static {
        doidMapper = new DiseaseMapper(ReactomeEvidence.class.getResourceAsStream("/DiseaseMapping.txt"));
    }

    private Map<Integer, String> doid2Other;

    public DiseaseMapper(InputStream file) {
        doid2Other = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] cols = line.split("\\t");
                    doid2Other.put(Integer.valueOf(cols[0].trim()), cols[1].trim());
                } catch (IndexOutOfBoundsException e) {
//                    logger.error(e);
                }
            }
            br.close();
        } catch (IOException e) {
//            logger.error(e);
        }
    }

    public String get(String doid) {
        return this.get(Integer.valueOf(doid));
    }

    public String get(Integer doid) {
        return doid2Other.get(doid);
    }
}
