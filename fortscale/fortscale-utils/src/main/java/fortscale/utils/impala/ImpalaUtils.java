package fortscale.utils.impala;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class ImpalaUtils {

    private static Logger logger = Logger.getLogger(ImpalaUtils.class);

    @Autowired
    private ImpalaClient impalaClient;

    /***
     *
     * This method drops a given list of tables from Impala
     *
     * @param tableNames  list of tables to drop
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean dropTables(Collection<String> tableNames, boolean doValidate) {
        int numberOfTablesDropped = 0;
        logger.debug("attempting to drop {} tables from impala", tableNames.size());
        for (String tableName: tableNames) {
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
     * This method returns a list of all of the tables starting with the given prefix
     *
     * @param prefix run with empty prefix to get all tables
     * @return
     */
    public Collection<String> getTablesWithPrefix(String prefix) {
        logger.debug("getting all tables with prefix {}", prefix);
        Collection<String> tableNames = impalaClient.getAllTables();
        logger.debug("found {} tables", tableNames.size());
        if (prefix.isEmpty()) {
            return tableNames;
        }
        Iterator<String> it = tableNames.iterator();
        logger.debug("filtering out tables not starting with {}", prefix);
        while (it.hasNext()) {
            String tableName = it.next();
            if (!tableName.startsWith(prefix)) {
                it.remove();
            }
        }
        logger.info("found {} tables with prefix {}", tableNames.size(), prefix);
        return tableNames;
    }

    /***
     *
     * This method drops all of the tables from Impala
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean dropAllTables(boolean doValidate) {
        Collection<String> tableNames = getTablesWithPrefix("");
        logger.debug("found {} tables to drop", tableNames.size());
        return dropTables(tableNames, doValidate);
    }

}