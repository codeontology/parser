package org.codeontology.frontend.builds;


import java.io.File;
import java.util.HashMap;


/**
 * Map exploration status of directories.
 */
public class AnalysisMap extends HashMap<File, ExplorationStatus> {

    private static AnalysisMap instance;


    public static AnalysisMap getInstance () {
        if (instance == null)
            instance = new AnalysisMap();
        return instance;
    }


    @Override
    public ExplorationStatus put(File root, ExplorationStatus status) {
        if (!canChangeStatus(get(root), status))
            throw new UnsupportedOperationException("Can't change from " + get(root) + " to " + status);
        super.put(root, status);
        return status;
    }


    /**
     * Check if a status change is possible.
     * @param from  The previous state.
     * @param to    The next state.
     * @return      True if state change is acceptable, false otherwise.
     */
    private boolean canChangeStatus (ExplorationStatus from, ExplorationStatus to) {
        return (from == null
            || from == to
            || from == ExplorationStatus.NOT_EXPLORED
            || (from == ExplorationStatus.EXPLORING && to == ExplorationStatus.EXPLORED));
    }

}
