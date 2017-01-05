package fortscale.utils.impala;

import fortscale.utils.test.impala.ImpalaTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

/**
 * Created by baraks on 1/2/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ImpalaClientTest {

    public static final String TEST_TABLE_NAME = "tableName";

    @Configuration
    @Import(ImpalaTestConfig.class)
    public static class springConfig
    {
    }
    @Autowired
    private ImpalaClient impalaClient;
    @Autowired
    private JdbcOperations impalaJdbcTemplate;

    @Test
    public void createTableTest()
    {
        impalaClient.createTable(TEST_TABLE_NAME,"fieldName1","fieldName1",",","/path",true);
        Set<String> allTables = impalaClient.getAllTables();
        Assert.assertTrue(allTables.contains(TEST_TABLE_NAME.toUpperCase()));
    }

    @Test
    public void dropTableTest()
    {
        createTableTest();
        impalaClient.dropTable(TEST_TABLE_NAME);
        Set<String> allTables = impalaClient.getAllTables();
        Assert.assertTrue(allTables.isEmpty());
    }

    @Test
    public void isTableExistsTest()
    {
        createTableTest();
        Assert.assertTrue(impalaClient.isTableExists(TEST_TABLE_NAME));
    }

    @Test
    public void insertAndQueryTest() throws Exception {
        createTableTest();
        String sql = "INSERT INTO " + TEST_TABLE_NAME +" VALUES ('field1_value')";
        impalaJdbcTemplate.execute(sql);
        Assert.assertNotNull(impalaJdbcTemplate.queryForList("select * from "+TEST_TABLE_NAME));
    }
}