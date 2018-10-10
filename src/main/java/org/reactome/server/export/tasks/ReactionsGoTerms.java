package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

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
        //{speciesName:"Homo sapiens"}??
        return " MATCH (rle:ReactionLikeEvent)-[:catalystActivity]->(ca:CatalystActivity)-[:activity]->(go:GO_MolecularFunction) " +
                "RETURN rle.stId AS Identifier, rle.displayName AS Name, go.databaseName + \":\" + go.accession AS GO_Term";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "Identifier", "Name", "GO_Term");
    }
}
