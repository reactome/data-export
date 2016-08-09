# Reactome Files Export Module

## What is the Reactome Files Export Module

The Files export module is used to create the exporting files based on queries to the Reactome Graph Database. The module acts as a collection of tasks run against the database using the graph core library. 

#### Project components used:

* Reactome graph library 

#### Project usage: 

The data-export can be executed by running the executable jar file. Please ensure that Neo4j database is running and correct properties are specified.

**Properties**

When executing the jar file following properties have to be set.

    -h  Reactome Neo4j host. DEFAULT: localhost
    -b  Reactome Neo4j port. DEFAULT: 7474
    -u  Reactome Neo4j user. DEFAULT: neo4j
    -p  Reactome Neo4j password. DEFAULT: neo4j
    -o  Output directori. DEFAULT: ./export
    -v  Verbose output 