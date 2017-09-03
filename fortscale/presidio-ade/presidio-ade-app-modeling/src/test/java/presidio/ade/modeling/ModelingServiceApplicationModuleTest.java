package presidio.ade.modeling;

import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.common.general.Schema;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.modeling.config.ModelingServiceApplicationModuleTestConfig;
import presidio.ade.test.utils.tests.BaseAppTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ModelingServiceApplicationModuleTest extends BaseAppTest {
    private static final String ENRICHED_RECORDS_LINE_FORMAT = "process --group_name enriched-record-models%s --session_id test-run --end_date 2017-01-01T00:00:00Z";
    private static final String ENRICHED_RECORDS_LINE = String.format(ENRICHED_RECORDS_LINE_FORMAT,"");
    private static final String FEATURE_AGGREGATION_RECORDS_LINE_FORMAT = "process --group_name feature-aggregation-record-models --session_id test-run --end_date 2017-01-01T00:00:00Z";
    private static final String FEATURE_AGGREGATION_RECORDS_LINE = String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT,"");
    private static final String SMART_RECORDS_LINE_FORMAT = "process --group_name smart-record-models --session_id test-run --end_date 2017-01-01T00:00:00Z";
    private static final String SMART_RECORDS_LINE = String.format(SMART_RECORDS_LINE_FORMAT,"");

    //Any supported schema should be added here.
    private static final List<String> expectedSchemas = Arrays.asList("file","authentication","active_directory");

    @Autowired
    private AslResourceFactory aslResourceFactory;
    @Value("${presidio.ade.modeling.enriched.records.base.configuration.path}")
    private String modelingEnrichedRecordsConfigurationPath;
    @Value("${presidio.ade.modeling.feature.aggregation.records.base.configuration.path}")
    private String modelingFeatureAggregationRecordsConfigurationPath;

    @Test
    public void enriched_records_configuration_and_feature_aggregation_rocords_configuration_contain_the_same_expected_schema_set(){
        Set<String> enrichedRecordsConfigurationFileSet = getEnrichedRecordsConfigurationFiles().collect(Collectors.toSet());
        Set<String> featureAggregationRecordConfigurationFileSet = getFeatureAggregationRecordsConfigurationFiles().collect(Collectors.toSet());
        Assert.assertEquals(enrichedRecordsConfigurationFileSet, featureAggregationRecordConfigurationFileSet);
        expectedSchemas.forEach(schema -> Assert.assertTrue(String.format("the schema %s is not supported",schema),enrichedRecordsConfigurationFileSet.contains(schema+".json")));
    }

    @Test
    public void enriched_records_configuration_path_contains_schema_configuration_files_test() {
        getEnrichedRecordsConfigurationFiles().forEach(enrichedConfFile -> assertIfNotBelongToSchema(enrichedConfFile));
    }

    @Test
    public void feature_aggregation_rocords_configuration_path_contains_schema_configuration_files_test() {
        getFeatureAggregationRecordsConfigurationFiles().forEach(enrichedConfFile -> assertIfNotBelongToSchema(enrichedConfFile));
    }




    @Test
    public void modeling_service_application_execute_enriched_records_sanity_test() {
        executeAndAssertCommandSuccess(ENRICHED_RECORDS_LINE);
    }

    @Test
    public void modeling_service_application_execute_file_records_sanity_test() {
        executeAndAssertCommandSuccess(String.format(ENRICHED_RECORDS_LINE_FORMAT,".file"));
    }

    @Test
    public void modeling_service_application_execute_authentication_records_sanity_test() {
        executeAndAssertCommandSuccess(String.format(ENRICHED_RECORDS_LINE_FORMAT,".AUTHENTICATION"));
    }

    @Test
    public void modeling_service_application_execute_active_directory_records_sanity_test() {
        executeAndAssertCommandSuccess(String.format(ENRICHED_RECORDS_LINE_FORMAT,".Active_directory"));
    }

    @Test
    public void modeling_service_application_execute_feature_aggregation_records_sanity_test() {
        executeAndAssertCommandSuccess(FEATURE_AGGREGATION_RECORDS_LINE);
    }

    @Test
    public void modeling_service_application_execute_file_feature_aggregation_records_sanity_test() {
        executeAndAssertCommandSuccess(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT,".FILE"));
    }

    @Test
    public void modeling_service_application_execute_authentication_feature_aggregation_records_sanity_test() {
        executeAndAssertCommandSuccess(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT,".Authentication"));
    }

    @Test
    public void modeling_service_application_execute_active_directory_feature_aggregation_records_sanity_test() {
        executeAndAssertCommandSuccess(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT,".active_directory"));
    }

//    @Test
//    public void modeling_service_application_smart_records_sanity_test() {
//		executeAndAssertCommandSuccess(SMART_RECORDS_LINE);
//    }


    private void assertIfNotBelongToSchema(String fileName){
        String[] fileNameSplit = fileName.split("\\.json");
        Assert.assertTrue(String.format("file name is not in the right format: %s", fileName), fileNameSplit.length == 1);
        Assert.assertTrue(String.format("file name is not in the right format: %s", fileName), Arrays.asList(Schema.values()).stream().anyMatch(schema -> schema.getName().equals(fileNameSplit[0])));
    }

    public Stream<String> getEnrichedRecordsConfigurationFiles(){
        return getFiles(modelingEnrichedRecordsConfigurationPath+"*");
    }

    public Stream<String> getFeatureAggregationRecordsConfigurationFiles(){
        return getFiles(modelingFeatureAggregationRecordsConfigurationPath+"*");
    }
    private Stream<String> getFiles(String confDir){
        return Arrays.stream(aslResourceFactory.getResources(confDir)).map(resource -> resource.getFilename());
    }

    @Override
    protected String getContextTestExecutionCommand() {
        return ENRICHED_RECORDS_LINE;
    }

    @Configuration
    @Import({ModelingServiceApplicationModuleTestConfig.class, BaseAppTest.springConfig.class,})
    protected static class springConfigModelingServiceApplication {

    }
}
