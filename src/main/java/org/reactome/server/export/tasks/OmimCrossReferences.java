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
public class OmimCrossReferences extends DataExportAbstract {

    @Override
    public String getName() {
        return "Reactome2OMIM";
    }

    @Override
    public String getQuery() {
        return " MATCH (rs:ReferenceIsoform)<-[:referenceEntity]-(pe:PhysicalEntity)<-[:input|output|repeatedUnit|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]-(:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway) " +
                "RETURN DISTINCT rs.variantIdentifier as ACC, p.stId AS PathwayId, p.displayName AS Pathway " +
                "ORDER BY ACC, PathwayId " +
                "UNION " +
                "MATCH (rs:ReferenceGeneProduct)<-[:referenceEntity]-(pe:PhysicalEntity)<-[:input|output|repeatedUnit|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]-(:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway) " +
                "WHERE NOT rs:ReferenceIsoform " +
                "RETURN DISTINCT rs.identifier as ACC, p.stId AS PathwayId, p.displayName AS Pathway " +
                "ORDER BY ACC, PathwayId";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "ACC", "PathwayId", "Pathway");
    }
}
