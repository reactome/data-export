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
public class DelawareBiotechnologyInstitute extends DataExportAbstract {
    @Override
    public String getName() {
        return "UniProt_Entity_Literature_Pathway_Species";
    }

    @Override
    public String getQuery() {
        return "MATCH (rd:ReferenceDatabase)<--(n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity), " +
                "      (pe)<-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|repeatedUnit|hasMember|hasCandidate|hasComponent*]-(rle:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway) " +
                "WHERE rd.displayName =~ \"UniProt\" " +
                "WITH n, pe, p, COLLECT(DISTINCT rle) AS rles " +
                "UNWIND rles AS rle " +
                "MATCH (rle)-[:literatureReference]->(lr:LiteratureReference) " +
                "RETURN DISTINCT n.identifier AS Identifier, " +
                "                pe.stId AS Entity, " +
                "                pe.displayName AS Entity_Name, " +
                "                lr.pubMedIdentifier AS PMID, " +
                "                p.stId AS Pathway, " +
                "                p.displayName AS Pathway_Name, " +
                "                p.speciesName AS Species " +
                "ORDER BY Identifier, Entity, Pathway";
//        return " MATCH (rs:ReferenceSequence{databaseName:\"UniProt\"})<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)-[:literatureReference]->(lr:LiteratureReference), " +
//                "      (ewas)<-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|repeatedUnit|hasMember|hasCandidate|hasComponent*]-(:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway) " +
//                "RETURN DISTINCT rs.identifier AS Identifier, " +
//                "                ewas.stId AS EWAS, " +
//                "                ewas.displayName AS EWAS_Name, " +
//                "                lr.pubMedIdentifier AS PMID, " +
//                "                p.stId AS Pathway, " +
//                "                p.displayName AS Pathway_Name, " +
//                "                p.speciesName AS Species " +
//                "ORDER BY Identifier, EWAS, Pathway";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "Identifier", "Entity", "Entity_Name", "PMID", "Pathway", "Pathway_Name", "Species");
    }
}
