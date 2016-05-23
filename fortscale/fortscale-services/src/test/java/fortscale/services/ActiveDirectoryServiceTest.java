package fortscale.services;

import fortscale.services.impl.ActiveDirectoryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ActiveDirectoryServiceTest {

    private static final String DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY = "system.activeDirectory.domainControllers";
    public static final String DOMAIN_CONTROLLERS_AS_STRING = "CN: DC1,CN: DC2";
    public static final ArrayList<String> DOMAIN_CONTROLLERS_AS_LIST = new ArrayList<>(Arrays.asList("CN: DC1", "CN: DC2"));

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;
    private ActiveDirectoryService activeDirectoryService;

    @Before
    public void setUp() throws Exception {


        activeDirectoryService = new ActiveDirectoryServiceImpl(applicationConfigurationService);

    }

    @Test
    public void testGetDomainControllersWhenDatabaseHasThem() throws Exception {
        final Optional<String> domainControllers = Optional.of(DOMAIN_CONTROLLERS_AS_STRING);
        Mockito.when(applicationConfigurationService.getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)).thenReturn(domainControllers);

        final ArrayList<String> expected = DOMAIN_CONTROLLERS_AS_LIST;
        final List<String> actual = activeDirectoryService.getDomainControllers();

        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testGetDomainControllersWhenADHasThem() throws Exception {
        // make database return nothing
        Mockito.when(applicationConfigurationService.getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)).thenReturn(Optional.of(""));

        //TODO: continue after DAO changes are pushed

        final ArrayList<String> expected = DOMAIN_CONTROLLERS_AS_LIST;
        final List<String> actual = activeDirectoryService.getDomainControllers();

        Assert.assertEquals(expected, actual);
    }


}
