package org.reactome.server.export.config;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.reactome.server.graph.config.Neo4jConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
@org.springframework.context.annotation.Configuration
@ComponentScan( basePackages = {"org.reactome.server.graph"} )
@EnableTransactionManagement
@EnableNeo4jRepositories( basePackages = {"org.reactome.server.graph.repository"} )
@EnableSpringConfigured
public class GraphQANeo4jConfig extends Neo4jConfig {
    private static final Logger logger = LoggerFactory.getLogger("importLogger");

    private SessionFactory sessionFactory;
    private Session session;

    @Bean
    public Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .setURI("http://".concat(System.getProperty("neo4j.host")).concat(":").concat(System.getProperty("neo4j.port")))
                .setCredentials(System.getProperty("neo4j.user"),System.getProperty("neo4j.password"));
        return config;
    }

    @Override
    @Bean
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            logger.info("Creating a Neo4j SessionFactory");
            sessionFactory = new SessionFactory(getConfiguration(), "org.reactome.server.graph.domain" );
        }
        return sessionFactory;
    }

    @Override
    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Session getSession() throws Exception {
        if (session == null){
            logger.info("Opening neo4j Session");
            session = super.getSession();
        }

        return session;
    }

}
