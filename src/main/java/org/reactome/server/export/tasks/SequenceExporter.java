package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
@DataExport
public class SequenceExporter extends DataExportAbstract {

    private static final String QUERY_IDS = "MATCH (rle:ReactionLikeEvent{speciesName:{speciesName}}) " +
                                            "OPTIONAL MATCH (rle)-[:input|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity), " +
                                            "               (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase{displayName:{refDb}}) " +
                                            "WITH rle, COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {uniprot: re.identifier, type:'input'} END) AS ps " +
                                            "OPTIONAL MATCH (rle)-[:output|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity), " +
                                            "               (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase{displayName:{refDb}}) " +
                                            "WITH rle, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {uniprot: re.identifier, type:'output'} END) AS ps " +
                                            "OPTIONAL MATCH (rle)-[:catalystActivity|physicalEntity|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity), " +
                                            "               (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase{displayName:{refDb}}) " +
                                            "WITH rle, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {uniprot: re.identifier, type:'catalyst'} END) AS ps " +
                                            "OPTIONAL MATCH (rle)-[:regulatedBy]->(:NegativeRegulation)-[:regulator|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity), " +
                                            "               (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase{displayName:{refDb}}) " +
                                            "WITH rle, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {uniprot: re.identifier, type:'negative'} END) AS ps " +
                                            "OPTIONAL MATCH (rle)-[:regulatedBy]->(:PositiveRegulation)-[:regulator|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity), " +
                                            "               (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase{displayName:{refDb}}) " +
                                            "WITH rle, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {uniprot: re.identifier, type:'positive'} END) AS ps " +
                                            "OPTIONAL MATCH path=(p:Pathway)-[:hasEvent]->(rle) " +
                                            "UNWIND ps AS part " +
                                            "RETURN p.stId AS pathway, rle.stId AS reaction, rle.displayName as reactionName, part.uniprot as uniprotId, collect (part.type) as rolesInReaction";

    @Override
    public String getQuery() {
        return QUERY_IDS;
    }

    @Override
    protected Map<String, Object> getMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("speciesName", "Homo sapiens");
        params.put("refDb", "UniProt");
        return params;
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, "pathway_id", "reaction_id", "reaction_name", "uniprot_acc", "role_in_reaction");
    }

    @Override
    public String getName() {
        return "reactome_reaction_exporter";
    }

    @Override
    public boolean printDbVersion() {
        return true;
    }
}
