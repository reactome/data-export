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
public class PathwaysRelationshipExport extends DataExportAbstract {

    @Override
    public String getName() {
        return "ReactomePathwaysRelation";
    }

    @Override
    public String getQuery() {
        //All pathways reachable from TopLevelPathway which have at least one pathway in the hasEvent slot
        return " MATCH (p:Pathway)-[:hasEvent]->(sp:Pathway) " +
                "WHERE (p)<-[:hasEvent*]-(:TopLevelPathway) OR (p:TopLevelPathway) " +
                "RETURN DISTINCT p.stId AS P_Identifier, sp.stId AS SP_Identifier, p.speciesName AS Species " +
                "ORDER BY p.speciesName, p.stId, sp.stId";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, false, "P_Identifier", "SP_Identifier");
    }
}
