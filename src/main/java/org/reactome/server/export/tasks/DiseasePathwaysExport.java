package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("unused")
@DataExport
public class DiseasePathwaysExport extends DataExportAbstract {

    @Override
    public String getName() {
        return "HumanDiseasePathways";
    }

    @Override
    public String getQuery() {
        return " MATCH (p:Pathway{isInDisease:true, speciesName:'Homo sapiens'}) " +
                "RETURN DISTINCT p.stId AS Identifier, p.displayName AS Name " +
                "ORDER BY p.stId, p.displayName";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, false, "Identifier", "Name");
    }
}
