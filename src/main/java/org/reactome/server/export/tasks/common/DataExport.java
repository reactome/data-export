package org.reactome.server.export.tasks.common;

import org.reactome.server.graph.service.GeneralService;

public interface DataExport {

    String getName();

    boolean run(GeneralService generalService, String path);

    default boolean printDbVersion() {
        return false;
    }
}
