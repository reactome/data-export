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
public class PathwaysExport extends DataExportAbstract {

    @Override
    public String getName() {
        return "ReactomePathways";
    }

    @Override
    public String getQuery() {
        //All pathways reachable from TopLevelPathway
        return " MATCH (p:Pathway) " +
                "WHERE (p)<-[:hasEvent*]-(:TopLevelPathway) OR (p:TopLevelPathway)" +
                "RETURN DISTINCT p.stId AS Identifier, p.displayName AS Name, p.speciesName AS Species " +
                "ORDER BY p.speciesName, p.displayName";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, false, "Identifier", "Name", "Species");
    }
}
