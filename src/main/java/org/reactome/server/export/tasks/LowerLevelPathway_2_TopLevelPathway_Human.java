package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Guilherme Viteri
 */
@SuppressWarnings("unused")
@DataExport
public class LowerLevelPathway_2_TopLevelPathway_Human extends DataExportAbstract {

    @Override
    public String getName() {
        return "LowerLevelPathway2Topic_Human";
    }

    @Override
    public String getQuery() {
        return " MATCH    (tlp:TopLevelPathway)-[:hasEvent*]->(llp:Pathway) " +
                "WHERE    tlp.speciesName = \"Homo sapiens\" " +
                "WITH     llp,tlp " +
                "RETURN   distinct llp.stId as SubPathwayID,  llp.displayName as SubPathwayName, tlp.stId as TopicID, tlp.displayName as TopicName, tlp.speciesName as Species " +
                "ORDER BY tlp.speciesName, tlp.displayName, llp.displayName";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, true, "SubPathwayID", "SubPathwayName", "TopicID", "TopicName", "Species");
    }
}
