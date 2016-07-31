package fortscale.web.rest;

import fortscale.domain.ad.AdConnection;
import fortscale.services.ApplicationConfigurationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ApiActiveDirectoryControllerTest {

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @InjectMocks
    private ApiActiveDirectoryController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testActiveDirectoryUpdate() {
        List<AdConnection> activeDirectoryConfigurations = new ArrayList<>();
        final String DOMAIN_PASSWORD="password";
        final String DC_NAME1="aaa";
        final String DC_NAME2="bbb";
        final String DOMAIN_BASE_SEARCH="search";
        final String DOMAIN_USER_NAME="user@user.com";
        final String ENCRYPTED_PASSWORD = "8bGagpbfO0hLMjKwrIc5SA==";
        AdConnection settings = new AdConnection();
        settings.setDomainPassword(DOMAIN_PASSWORD);
        settings.setDcs(Arrays.asList(DC_NAME1,DC_NAME2));
        settings.setDomainBaseSearch(DOMAIN_BASE_SEARCH);
        settings.setDomainUser(DOMAIN_USER_NAME);
        activeDirectoryConfigurations.add(settings);
        controller.updateActiveDirectory(activeDirectoryConfigurations);
        ArgumentCaptor<String> argumentKey = ArgumentCaptor.forClass(String.class);
        Class<List<AdConnection>> adConnectionListClass = (Class<List<AdConnection>>) Collections.<AdConnection>emptyList().
				getClass();
        ArgumentCaptor<List<AdConnection>> argumentValue = ArgumentCaptor.forClass(adConnectionListClass);
        verify(applicationConfigurationService, times(1)).updateConfigItemAsObject(argumentKey.capture(),
				argumentValue.capture());
        Assert.assertEquals("system.activeDirectory.settings",argumentKey.getValue());
        Assert.assertEquals(1,argumentValue.getValue().size()); //Check that we have 1 connection in string
        AdConnection argumentConnection1 = argumentValue.getValue().get(0);
        Assert.assertEquals(2,argumentConnection1.getDcs().size());
        Assert.assertEquals(DOMAIN_BASE_SEARCH,argumentConnection1.getDomainBaseSearch());
        Assert.assertEquals(DOMAIN_USER_NAME,argumentConnection1.getDomainUser());
        Assert.assertEquals(ENCRYPTED_PASSWORD,argumentConnection1.getDomainPassword());
    }

    @Test
    public void testActiveDirectoryUpdate_encrypt_password() {
        final String DOMAIN_PASSWORD="password";
        final String DC_NAME1="aaa";
        final String DC_NAME2="bbb";
        final String DOMAIN_BASE_SEARCH="search";
        final String DOMAIN_USER_NAME="user@domain.com";
        final String ENCRYPTED_PASSWORD = "8bGagpbfO0hLMjKwrIc5SA==";
        AdConnection oldSettings = new AdConnection();
        oldSettings.setDomainPassword(DOMAIN_PASSWORD+"1111");
        oldSettings.setDomainUser(DOMAIN_USER_NAME);
        Mockito.when(applicationConfigurationService.
                getApplicationConfigurationAsObjects("system.activeDirectory.settings", AdConnection.class)).
                thenReturn(Arrays.asList(oldSettings));
        AdConnection settings = new AdConnection();
        settings.setDomainPassword(DOMAIN_PASSWORD);
        settings.setDcs(Arrays.asList(DC_NAME1,DC_NAME2));
        settings.setDomainBaseSearch(DOMAIN_BASE_SEARCH);
        settings.setDomainUser(DOMAIN_USER_NAME);
        List<AdConnection> activeDirectoryConfigurations = new ArrayList<>();
        activeDirectoryConfigurations.add(settings);
        controller.updateActiveDirectory(activeDirectoryConfigurations);
        ArgumentCaptor<String> argumentKey = ArgumentCaptor.forClass(String.class);
        Class<List<AdConnection>> adConnectionListClass = (Class<List<AdConnection>>) Collections.<AdConnection>emptyList().
				getClass();
        ArgumentCaptor<List<AdConnection>> argumentValue = ArgumentCaptor.forClass(adConnectionListClass);
        verify(applicationConfigurationService, times(1)).updateConfigItemAsObject(argumentKey.capture(),
				argumentValue.capture());
        AdConnection argumentConnection1 = argumentValue.getValue().get(0);
        Assert.assertEquals(ENCRYPTED_PASSWORD,argumentConnection1.getDomainPassword());
    }

    @Test
    public void testActiveDirectoryUpdate_do_not_encrypt_password() {
        final String DC_NAME1="aaa";
        final String DC_NAME2="bbb";
        final String DOMAIN_BASE_SEARCH="search";
        final String DOMAIN_USER_NAME="user@domain.com";
        final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";
        AdConnection oldSettings = new AdConnection();
        oldSettings.setDomainPassword(ENCRYPTED_PASSWORD);
        oldSettings.setDomainUser(DOMAIN_USER_NAME);
        Mockito.when(applicationConfigurationService.
				getApplicationConfigurationAsObjects("system.activeDirectory.settings", AdConnection.class)).
				thenReturn(Arrays.asList(oldSettings));
        AdConnection settings = new AdConnection();
        settings.setDomainPassword(ENCRYPTED_PASSWORD);
        settings.setDcs(Arrays.asList(DC_NAME1,DC_NAME2));
        settings.setDomainBaseSearch(DOMAIN_BASE_SEARCH);
        settings.setDomainUser(DOMAIN_USER_NAME);
        List<AdConnection> activeDirectoryConfigurations = new ArrayList<>();
        activeDirectoryConfigurations.add(settings);
        controller.updateActiveDirectory(activeDirectoryConfigurations);
        ArgumentCaptor<String> argumentKey = ArgumentCaptor.forClass(String.class);
        Class<List<AdConnection>> adConnectionListClass = (Class<List<AdConnection>>) Collections.<AdConnection>emptyList().
				getClass();
        ArgumentCaptor<List<AdConnection>> argumentValue = ArgumentCaptor.forClass(adConnectionListClass);
        verify(applicationConfigurationService, times(1)).updateConfigItemAsObject(argumentKey.capture(), argumentValue.capture());
        AdConnection argumentConnection1 = argumentValue.getValue().get(0);
        Assert.assertEquals(ENCRYPTED_PASSWORD,argumentConnection1.getDomainPassword());
    }

}