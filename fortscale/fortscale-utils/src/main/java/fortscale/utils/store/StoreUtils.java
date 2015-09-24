package fortscale.utils.store;

import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Amir Keren on 24/09/15.
 */
public class StoreUtils {

    private static Logger logger = Logger.getLogger(StoreUtils.class);

    //TODO - is this correct? if so, extract this to properties file
    private final String stateBaseFolder = "/home/cloudera/fortscale/streaming/state";

    /***
     *
     * This method deletes all of the states from the state folder
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteAllStates(boolean doValidate) {
        Collection<String> states = getStatesWithPrefix("");
        logger.debug("found {} states to delete", states.size());
        return deleteStates(states, doValidate);
    }

    /***
     *
     * This method deletes a given list of states
     *
     * @param states      list of states to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteStates(Collection<String> states, boolean doValidate) {
        int numberOfStatesDeleted = 0;
        logger.debug("attempting to delete {} states", states.size());
        for (String state: states) {
            File stateDirectory = new File(stateBaseFolder + "/" + state);
            try {
                FileUtils.deleteDirectory(stateDirectory);
            } catch (IOException ex) {
                logger.debug("failed to delete state {} - {}", state, ex);
            }
            if (doValidate) {
                if (!stateDirectory.exists()) {
                    logger.info("deleted state {}", state);
                    numberOfStatesDeleted++;
                } else {
                    logger.error("failed to delete state {}", state);
                }
            }
        }
        if (numberOfStatesDeleted == states.size()) {
            logger.info("deleted all {} states", states.size());
            return true;
        }
        logger.error("failed to delete all {} tables, deleted only {}", states.size(), numberOfStatesDeleted);
        return false;
    }

    /***
     *
     * This method returns a list of all of the states starting with the given prefix
     *
     * @param prefix run with empty prefix to get all states
     * @return
     */
    public Collection<String> getStatesWithPrefix(final String prefix) {
        logger.debug("getting all states with prefix {}", prefix);
        File file = new File(stateBaseFolder);
        String[] states = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory() && name.startsWith(prefix);
            }
        });
        logger.debug("found {} states", states.length);
        return Arrays.asList(states);
    }

}