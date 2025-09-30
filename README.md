[<img src=https://user-images.githubusercontent.com/6883670/31999264-976dfb86-b98a-11e7-9432-0316345a72ea.png height=75 />](https://reactome.org)

# Reactome Files Export Module

## What is the Reactome Files Export Module

The Files export module is used to create the exporting files based on queries to the Reactome Graph Database. The module acts as a collection of tasks run against the database using the graph core library. 


#### Project components used:

* [Reactome Graph Library](https://github.com/reactome/graph-core)

#### Project usage: 

The data-export can be executed by running the executable jar file. Please ensure that Neo4j database is running and correct properties are specified.

**Properties**

When executing the jar file the following properties have to be set.

```console
-h  Reactome Neo4j host. DEFAULT: bolt://localhost:7687
-u  Reactome Neo4j user. DEFAULT: neo4j
-p  Reactome Neo4j password. DEFAULT: neo4j
-o  Output directory. DEFAULT: ./export
-v  Verbose output

java -Xmx10G -jar target/data-exporter-exec.jar <arguments> 
```

**Recommendations**

Ensembl mapping file may cause ```java.lang.OutOfMemoryError: GC overhead limit exceeded``` 
due to excessive data returned in its query. Before you experience that, we recommend that you adjust memory setting to ```-Xmx10G```

### Extras

#### Open Targets Schema Validator [validator](https://github.com/opentargets/validator)

```console
export OBJC_DISABLE_INITIALIZE_FORK_SAFETY=YES; cat file.json | opentargets_validator --schema https://raw.githubusercontent.com/opentargets/json_schema/1.6.2/opentargets.json
```
