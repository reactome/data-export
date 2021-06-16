package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class ReactionsGoTerms extends DataExportAbstract {

    @Override
    public String getName() {
        return "Reactions2GoTerms_human";
    }

    @Override
    public String getQuery() {
        return " MATCH (rle:ReactionLikeEvent)-[:catalystActivity]->(ca:CatalystActivity)-[:activity]->(go:GO_MolecularFunction) " +
                "WHERE rle.stId CONTAINS 'R-HSA-' " +
                "RETURN rle.stId AS Identifier, rle.displayName AS Name, go.databaseName + ':' + go.accession AS GO_Term";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "Identifier", "Name", "GO_Term");
    }
}
