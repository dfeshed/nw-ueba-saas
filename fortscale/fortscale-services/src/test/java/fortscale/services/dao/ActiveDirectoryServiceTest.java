package fortscale.services.dao;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.dao.ActiveDirectoryDAO;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.impl.ActiveDirectoryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActiveDirectoryServiceTest {

    private static final String DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY = "system.activeDirectory.domainControllers";
    private static final String DOMAIN_CONTROLLERS_AS_STRING = "CN: DC1,CN: DC2";
    private static final ArrayList<String> DOMAIN_CONTROLLERS_AS_LIST = new ArrayList<>(Arrays.asList(DOMAIN_CONTROLLERS_AS_STRING.split(",")));
    private static final List<AdConnection> AD_CONNECTIONS = Stream.of(new AdConnection(), new AdConnection()).collect(Collectors.toList());


    @Mock
    private ApplicationConfigurationService applicationConfigurationService;
    @Mock
    private ActiveDirectoryDAO activeDirectoryDAO;

    private ActiveDirectoryService testedActiveDirectoryService;

    @Before
    public void setUp() throws Exception {
        // using doReturn instead of when+thenReturn because they don't handle returning generic lists well
        doReturn(AD_CONNECTIONS).when(applicationConfigurationService).
				getApplicationConfigurationAsObjects(AdConnection.ACTIVE_DIRECTORY_KEY, AdConnection.class);
        // we don't want to actually save anything
        doNothing().when(applicationConfigurationService).insertConfigItem(any(), any());

        testedActiveDirectoryService = new ActiveDirectoryServiceImpl(activeDirectoryDAO, applicationConfigurationService);
    }

    @Test
    public void testGetDomainControllersWhenWeGetThemFromDatabase() throws Exception {
        final Optional<String> domainControllers = Optional.of(DOMAIN_CONTROLLERS_AS_STRING);
        when(applicationConfigurationService.getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)).thenReturn(domainControllers);

        final ArrayList<String> expected = DOMAIN_CONTROLLERS_AS_LIST;
        final List<String> actual = testedActiveDirectoryService.getDomainControllers();

        Assert.assertEquals(expected, actual);
        verify(applicationConfigurationService, never()).insertConfigItem(any(), any()); //make sure we didn't save
    }


    @Test
    public void testGetDomainControllersWhenWeGetThemFromActiveDirectory() throws Exception {
        // make database return nothing
        when(applicationConfigurationService.getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)).thenReturn(Optional.empty());
        // using doReturn instead of when+thenReturn because they don't handle returning generic lists well
        doReturn(DOMAIN_CONTROLLERS_AS_LIST).when(activeDirectoryDAO).getDomainControllers(AD_CONNECTIONS);

        final ArrayList<String> expected = DOMAIN_CONTROLLERS_AS_LIST;
        final List<String> actual = testedActiveDirectoryService.getDomainControllers();

        Assert.assertEquals(expected, actual);
        verify(applicationConfigurationService).insertConfigItem(any(), any()); //make sure we saved (once)
    }

    @Test
    public void testGetAdConnectionsFromDatabase() throws Exception {
        final List<AdConnection> expected = AD_CONNECTIONS;
        final List<AdConnection> actual = testedActiveDirectoryService.getAdConnectionsFromDatabase();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFromActiveDirectory() throws Exception {
        String filter = "some filter";
        String adFields = "some adFields";
        int resultLimit = 999;
        ActiveDirectoryResultHandler handler = mock(ActiveDirectoryResultHandler.class);

        testedActiveDirectoryService.getFromActiveDirectory(filter, adFields, resultLimit, handler);
        
        verify(activeDirectoryDAO).getAndHandle(filter, adFields, resultLimit, handler, AD_CONNECTIONS);
    }
}
