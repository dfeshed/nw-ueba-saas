package fortscale.utils.impala;

import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class ImpalaUtils extends CleanupDeletionUtil {

    private static Logger logger = Logger.getLogger(ImpalaUtils.class);

    @Autowired
    private ImpalaClient impalaClient;

    /***
     *
     * This method returns all of the tables in Impala
     *
     * @return
     */
    @Override
    public Collection<String> getAllEntities() {
        return impalaClient.getAllTables();
    }

    /***
     *
     * This method drops a given list of tables from Impala
     *
     * @param tableNames  list of tables to drop
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    @Override
    public boolean deleteEntities(Collection<String> tableNames, boolean doValidate) {
        int numberOfTablesDropped = 0;
        logger.debug("attempting to drop {} tables from impala", tableNames.size());
        for (String tableName : tableNames) {
            impalaClient.dropTable(tableName);
            if (doValidate) {
                //verify drop
                if (impalaClient.isTableExists(tableName)) {
                    logger.error("failed to drop table {}", tableName);
                } else {
                    logger.info("dropped table {}", tableName);
                    numberOfTablesDropped++;
                }
            } else {
                numberOfTablesDropped++;
            }
        }
        if (numberOfTablesDropped == tableNames.size()) {
            logger.info("dropped all {} tables", tableNames.size());
            return true;
        }
        logger.error("failed to drop all {} tables, dropped only {}", tableNames.size(), numberOfTablesDropped);
        return false;
    }

    /***
     *
     * This method drops all of the tables from Impala
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    @Override
    public boolean deleteAllEntities(boolean doValidate) {
        Collection<String> tableNames = getAllEntities();
        logger.debug("found {} tables to drop", tableNames.size());
        return deleteEntities(tableNames, doValidate);
    }

}