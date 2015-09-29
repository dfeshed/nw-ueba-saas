package fortscale.utils.store;

import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Amir Keren on 24/09/15.
 */
public class StoreUtils extends CleanupDeletionUtil {

    private static Logger logger = Logger.getLogger(StoreUtils.class);

    @Value("${fortscale.home.dir}/streaming/state")
    private String stateBaseFolder;

    /***
     *
     * This method deletes all of the states from the store
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteAllStates(boolean doValidate) {
        Collection<String> states = getAllEntities();
        logger.debug("found {} states to delete", states.size());
        return deleteEntities(states, doValidate);
    }

    /***
     *
     * This method returns all of the states in the store
     *
     * @return
     */
    @Override
    public Collection<String> getAllEntities() {
        File file = new File(stateBaseFolder);
        String[] states = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        logger.debug("found {} states", states.length);
        return Arrays.asList(states);
    }

    /***
     *
     * This method deletes a given list of states
     *
     * @param states      list of states to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    @Override
    public boolean deleteEntities(Collection<String> states, boolean doValidate) {
        int numberOfStatesDeleted = 0;
        logger.debug("attempting to delete {} states", states.size());
        for (String state: states) {
            File stateDirectory = new File(stateBaseFolder + "/" + state);
            if (stateDirectory.exists()) {
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
            } else {
                logger.warn("state {} doesn't exist", state);
            }
        }
        if (numberOfStatesDeleted == states.size()) {
            logger.info("deleted all {} states", states.size());
            return true;
        }
        logger.error("failed to delete all {} tables, deleted only {}", states.size(), numberOfStatesDeleted);
        return false;
    }

}