package org.reactome.server.export.common;

import org.reactome.server.graph.service.GeneralService;

public interface DataExport {
    boolean run(GeneralService genericService);
}
