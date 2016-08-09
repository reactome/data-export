package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class DelawareBiotechnologyInstitute extends DataExportAbstract {
    @Override
    public String getName() {
        return "Reactome2DelawareBiotechnologyInstitute";
    }

    @Override
    public String getQuery() {
        return " MATCH (rs:ReferenceSequence{databaseName:\"UniProt\"})<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)-[:literatureReference]->(lr:LiteratureReference), " +
                "      (ewas)<-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|repeatedUnit|hasMember|hasCandidate|hasComponent*]-(:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway) " +
                "RETURN DISTINCT rs.identifier AS ACC, ewas.stId AS EWAS_stId, lr.pubMedIdentifier AS PMID, p.stId AS Pathway_stId";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path,"ACC", "EWAS_stId", "PMID", "Pathway_stId");
    }
}
