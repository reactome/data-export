package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;
import org.reactome.server.export.tasks.result.ComplexParticipants;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class ComplexParticipantsPubMedIdentifiers extends DataExportAbstract {

    private static final Logger logger = LoggerFactory.getLogger("exportLogger");

    @Override
    public String getName() {
        return "ComplexParticipantsPubMedIdentifiers_human";
    }

    @Override
    public String getQuery() {
        return " MATCH (c:Complex{speciesName:\"Homo sapiens\"})-[:hasComponent|hasMember|hasCandidate|repeatedUnit*]->()-[:referenceEntity]->(re:ReferenceEntity) " +
                "OPTIONAL MATCH (c)-[:summation|literatureReference*]->(lr1:LiteratureReference) " +
                "OPTIONAL MATCH (c)<-[:output]-(:ReactionLikeEvent)-[:summation|literatureReference*]->(lr2:LiteratureReference) " +
                "OPTIONAL MATCH (c)-[:hasComponent*]->(pc:Complex) " +
                "RETURN c.stId AS identifier, " +
                "       c.displayName AS name, " +
                "       COLLECT(DISTINCT pc.stId) AS participatingComplexes," +
                "       COLLECT(DISTINCT { " +
                "           dbName: re.databaseName, " +
                "           id: CASE WHEN NOT re.variantIdentifier IS NULL THEN re.variantIdentifier ELSE re.identifier END " +
                "         }) AS participants, " +
                "       COLLECT(DISTINCT lr1.pubMedIdentifier) + COLLECT(DISTINCT lr2.pubMedIdentifier) as pubMedIdentifiers " +
                "ORDER BY identifier";
    }

    @Override
    public void printResult(Result result, Path path) { /*Nothing here*/ }

    public boolean run(GeneralService genericService, String path) {
        if (doTest()) {
            try {
                AdvancedDatabaseObjectService ados = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);
                Collection<ComplexParticipants> res = ados.getCustomQueryResults(ComplexParticipants.class, getQuery(), getMap());
                List<String> lines = res.stream().map(ComplexParticipants::toString).collect(Collectors.toList());
                if (!lines.isEmpty()) {
                    lines.add(0, "identifier\tname\tparticipants\tparticipatingComplex\tpubMedIdentifiers");
                    Files.write(createFile(path), lines, StandardCharsets.UTF_8);
                    return true;
                }
            } catch (CustomQueryException | IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }
}
