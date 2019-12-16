package org.reactome.server.export.tasks.common;

import org.reactome.server.graph.service.GeneralService;

public interface DataExport {

    /**
     * @return task name
     */
    String getName();

    boolean run(GeneralService generalService, String path);

    /**
     * @return true if you want the current release version to be added in the filename
     */
    default boolean printDbVersion() {
        return false;
    }

    /**
     * @return true if you want to add null values as -
     *         set to false accordingly in the given task if you don't want null (-) values to appear in the file.
     */
    default boolean addNullValues() {
        return true;
    }
}
