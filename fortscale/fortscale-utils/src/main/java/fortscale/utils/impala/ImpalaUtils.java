package fortscale.utils.impala;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class ImpalaUtils {

    private static Logger logger = Logger.getLogger(ImpalaUtils.class);

    @Autowired
    private ImpalaClient impalaClient;

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

    //run with empty prefix to get all tables
    public Collection<String> getAllTablesWithPrefix(String prefix) {
        logger.debug("getting all tables");
        Collection<String> tableNames = impalaClient.getAllTables();
        logger.debug("found {} tables", tableNames.size());
        if (prefix.isEmpty()) {
            return tableNames;
        }
        Iterator<String> it = tableNames.iterator();
        logger.debug("filtering out tables not starting with {}", prefix);
        while (it.hasNext()) {
            String collectionName = it.next();
            if (!collectionName.startsWith(prefix)) {
                it.remove();
            }
        }
        logger.info("found {} tables with prefix {}", tableNames.size(), prefix);
        return tableNames;
    }

    public boolean dropAllTables(boolean doValidate) {
        Collection<String> tableNames = getAllTablesWithPrefix("");
        logger.debug("found {} tables to drop", tableNames.size());
        return dropTables(tableNames, doValidate);
    }

}