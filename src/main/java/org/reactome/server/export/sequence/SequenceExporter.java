package org.reactome.server.export.sequence;

import org.reactome.server.export.sequence.model.SequenceReaction;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class SequenceExporter {
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

    public static void export(AdvancedDatabaseObjectService service, String path) {
        exportReactions(service, path);
    }

    private static void exportReactions(AdvancedDatabaseObjectService service, String path) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("speciesName", "Homo sapiens");
            params.put("refDb", "UniProt");
            Collection<SequenceReaction> reactions = service.getCustomQueryResults(SequenceReaction.class, QUERY_IDS, params);
            String fileName = path + "reactome_reaction_exporter.txt";
            saveSequenceFile(fileName, reactions);
        } catch (CustomQueryException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void saveSequenceFile(String fileName, Collection<SequenceReaction> reactions) throws FileNotFoundException {
        PrintStream ps = new PrintStream(new FileOutputStream(new File(fileName)));
        for (SequenceReaction reaction : reactions) {
            if (reaction.getPathway() != null) {
                ps.println(reaction.getTabularFormat());
            }
        }
        ps.close();
    }
}
