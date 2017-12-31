package presidio.adapter.services.impl;

import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.adapter.util.AdapterConfigurationUtil;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class FlumeAdapterExecutionServiceTest {


    private PresidioExecutionService flumeAdapterExecutionService;
    private ProcessExecutor mockProcessExecutor;
    private FlumeConfigurationUtil mockFlumeConfigurationUtil;
    private AdapterConfigurationUtil mockAdapterConfigurationUtil;
    private MongoTemplate mockedMongoTemplate;
    private PresidioInputPersistencyService mockPresidioInputPersistencyService;
    private String mockedFlumeHome;
    private String mockedConfFolder;
    private String mockedPropertiesFile;
    private String mockedAgent;
    private String mockedAfterTestsFilePath;
    private String mockedJobName;
    private String mockedFlumeExecutionScriptPath;


    @Before
    public void setUp() throws Exception {

        mockProcessExecutor = Mockito.mock(ProcessExecutor.class);
        Mockito.when(mockProcessExecutor.executeProcess(Mockito.anyString(), Mockito.anyListOf(String.class), Mockito.anyString())).thenReturn(0);

        mockFlumeConfigurationUtil = Mockito.mock(FlumeConfigurationUtil.class);
        mockAdapterConfigurationUtil = Mockito.mock(AdapterConfigurationUtil.class);
        mockedMongoTemplate = Mockito.mock(MongoTemplate.class);
        mockedFlumeHome = "some_flume_home" + File.separator;
        mockedConfFolder = mockedFlumeHome + "conf" + File.separator;
        mockedPropertiesFile = Paths.get("active_directory.properties").normalize().toString();
        mockedAgent = "mockedAgent";
        mockedAfterTestsFilePath = mockedConfFolder + "after_test_" + mockedPropertiesFile;
        mockedJobName = "active_directory.properties";
        mockedFlumeExecutionScriptPath = mockedFlumeHome + "bin" + File.separator + "flume-ng";
        Mockito.when(mockFlumeConfigurationUtil.getFlumeHome()).thenReturn(mockedFlumeHome);
        Mockito.when(mockFlumeConfigurationUtil.createConfFolderPath()).thenReturn(mockedConfFolder);
        Mockito.when(mockFlumeConfigurationUtil.createConfFileName(Mockito.any(Schema.class))).thenReturn(mockedPropertiesFile);
        Mockito.when(mockFlumeConfigurationUtil.createAgentName(Mockito.any(Schema.class))).thenReturn(mockedAgent);
        Mockito.when(mockFlumeConfigurationUtil.getFlumeExecutionScriptPath()).thenReturn(mockedFlumeExecutionScriptPath);
        Mockito.when(mockFlumeConfigurationUtil.getExecuteAgentCommand()).thenReturn("agent");
        Mockito.when(mockFlumeConfigurationUtil.getAgentNameArgument(Mockito.any(Schema.class))).thenReturn("--name " + mockedAgent);
        Mockito.when(mockFlumeConfigurationUtil.getConfFolderArgument()).thenReturn("--conf " + mockedConfFolder);
        Mockito.when(mockFlumeConfigurationUtil.getConfFilePathArgument(Mockito.any(Schema.class))).thenReturn("--conf-file " + mockedPropertiesFile);
        Mockito.when(mockFlumeConfigurationUtil.createJobName(Mockito.any(Schema.class), Mockito.any(Instant.class), Mockito.any(Instant.class))).thenReturn(mockedJobName);
        Mockito.when(mockFlumeConfigurationUtil.getFlumeExecutionConfFileArgument(mockedAfterTestsFilePath)).thenReturn("--conf-file " + mockedAfterTestsFilePath);
        Mockito.when(mockFlumeConfigurationUtil.createExecutionConfFile(Mockito.any(Schema.class), Mockito.any(Instant.class), Mockito.any(Instant.class))).thenReturn(mockedAfterTestsFilePath);

        flumeAdapterExecutionService = new FlumeAdapterExecutionService(mockProcessExecutor, mockFlumeConfigurationUtil, mockAdapterConfigurationUtil, mockPresidioInputPersistencyService, mockedMongoTemplate);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void clean() throws Exception {
        flumeAdapterExecutionService.retentionClean(null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cleanAll() throws Exception {
        flumeAdapterExecutionService.cleanAll(null);
    }

    @Test
    public void run() throws Exception {
        Instant startDate = Instant.EPOCH; //1970-01-01T00:00:00Z
        Instant endDate = startDate.plus((Duration.ofDays(365))); //1971-01-01T00:00:00Z
        flumeAdapterExecutionService.run(Schema.ACTIVE_DIRECTORY, startDate, endDate, null);

        final List<String> expectedArgumentList = Arrays.asList(mockedFlumeExecutionScriptPath, "agent", "--name " + mockedAgent, "--conf", mockedConfFolder, "--conf-file", mockedAfterTestsFilePath);
        Mockito.verify(mockProcessExecutor).executeProcess(mockedJobName, expectedArgumentList, mockedFlumeHome);
    }

}