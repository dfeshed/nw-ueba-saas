package fortscale.utils.test.impala;

import fortscale.utils.impala.ImpalaClient;
import org.springframework.util.Assert;

/**
 * Created by baraks on 1/2/2017.
 */
public class ImpalaTestClient extends ImpalaClient {
    /**
     * does nothing. no need to refresh table in embedded db
     * @param tableName
     */
    @Override
    public void refreshTable(String tableName)
    {

    }

    /**
     * does nothing. no need to ad partitions in embedded db
     * @param tableName
     * @param partition
     */
    @Override
    public void addPartitionToTable(String tableName, String partition)
    {

    }

    @Override
    public void createTable(String tableName, String fields, String partition, String delimiter, String location,
                            boolean onlyIfNotExist)
    {
        Assert.hasText(tableName);
        Assert.hasText(fields);
        Assert.hasText(delimiter);
        Assert.hasText(location);

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        if (onlyIfNotExist) {
            builder.append(" IF NOT EXISTS ");
        }
        builder.append(tableName).append("(").append(fields).append("  VARCHAR(255))");
        impalaJdbcTemplate.execute(builder.toString());

    }

    @Override
    protected String tableNameColumn() {
        return "TABLE_NAME";
    }

    @Override
    public boolean isTableExists(String tableViewName)
    {
        return getAllTables().contains(tableViewName.toUpperCase());
    }
}
