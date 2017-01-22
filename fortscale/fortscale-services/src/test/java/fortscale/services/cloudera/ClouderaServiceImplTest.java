package fortscale.services.cloudera;

import com.cloudera.api.model.ApiHealthSummary;
import com.cloudera.api.model.ApiRole;
import com.cloudera.api.model.ApiRoleList;
import com.cloudera.api.model.ApiRoleState;
import com.cloudera.api.v10.RoleCommandsResourceV10;
import com.cloudera.api.v10.ServicesResourceV10;
import com.cloudera.api.v8.RolesResourceV8;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;

/**
 * Created by barak_schuster on 1/17/17.
 */
public class ClouderaServiceImplTest {

    private final String serviceName = "foo-service";
    private ClouderaServiceImpl clouderaService;

    public void setup(Duration roleStartTimeout,Duration roleStopTimeout )
    {
        clouderaService = new ClouderaServiceImpl("host","cluster","user","pass", roleStartTimeout, roleStopTimeout, Duration.ofSeconds(1));
        ServicesResourceV10 servicesRes = Mockito.mock(ServicesResourceV10.class);
        RolesResourceV8 rolesResource = Mockito.mock(RolesResourceV8.class);
        ApiRoleList roles = new ApiRoleList();
        ApiRole item = Mockito.mock(ApiRole.class);
        Mockito.when(item.getName()).thenReturn("bar-role");
        Mockito.when(item.getRoleState()).thenReturn(ApiRoleState.STOPPED).thenReturn(ApiRoleState.STARTED);
        Mockito.when(item.getHealthSummary()).thenReturn(ApiHealthSummary.GOOD);
        roles.add(item);
        ApiRole item2 = Mockito.mock(ApiRole.class);
        Mockito.when(item2.getName()).thenReturn("goo-role");
        Mockito.when(item2.getHealthSummary()).thenReturn(ApiHealthSummary.GOOD);
        Mockito.when(item2.getRoleState()).thenReturn(ApiRoleState.STOPPED).thenReturn(ApiRoleState.STARTED);
        roles.add(item2);
        Mockito.when(rolesResource.readRoles()).thenReturn(roles);
        Mockito.when(rolesResource.readRole(item.getName())).thenReturn(item);
        Mockito.when(rolesResource.readRole(item2.getName())).thenReturn(item2);
        RoleCommandsResourceV10 roleCommandsResourceV10 = Mockito.mock(RoleCommandsResourceV10.class);
        Mockito.when(servicesRes.getRoleCommandsResource(serviceName)).thenReturn(roleCommandsResourceV10);
        clouderaService.setServicesRes(servicesRes);
        Mockito.when(servicesRes.getRolesResource(serviceName)).thenReturn(rolesResource);

    }
    @Test
    public void shouldStartStoppedService() throws Exception {
        Duration roleStartTimeout = Duration.ofMillis(1);
        Duration roleStopTimeout = Duration.ofMillis(1);
        setup(roleStartTimeout,roleStopTimeout);

        boolean startedSuccessfully = clouderaService.start(serviceName);
        Assert.assertTrue(startedSuccessfully);
    }

    @Test
    public void shouldFaileToStartStoppedServiceDueToTimeout() throws Exception {
        Duration roleStartTimeout = Duration.ZERO;
        Duration roleStopTimeout = Duration.ZERO;
        setup(roleStartTimeout,roleStopTimeout);

        boolean startedSuccessfully = clouderaService.start(serviceName);
        Assert.assertFalse(startedSuccessfully);
    }

}