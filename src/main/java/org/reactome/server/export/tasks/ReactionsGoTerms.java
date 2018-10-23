package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class ReactionsGoTerms extends DataExportAbstract {

    private static final String HOMO_SAPIENS_TAX_ID = "9606";

    @Override
    public String getName() {
        return "Reactions2GoTerms_human";
    }

    @Override
    protected Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("taxId", HOMO_SAPIENS_TAX_ID);
        return map;
    }

    @Override
    public String getQuery() {
        return " MATCH (s:Species)<-[:species]-(rle:ReactionLikeEvent)-[:catalystActivity]->(ca:CatalystActivity)-[:activity]->(go:GO_MolecularFunction) " +
                "WITH DISTINCT rle, go, COLLECT(DISTINCT s) AS species " +
                "WHERE SIZE(species) = 1 AND SINGLE(x IN species WHERE x.taxId = {taxId}) " +
                "RETURN rle.stId AS Identifier, rle.displayName AS Name, go.databaseName + ':' + go.accession AS GO_Term";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "Identifier", "Name", "GO_Term");
    }
}
