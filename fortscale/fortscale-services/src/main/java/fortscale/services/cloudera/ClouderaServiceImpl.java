package fortscale.services.cloudera;

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.*;
import com.cloudera.api.v10.RoleCommandsResourceV10;
import com.cloudera.api.v10.RootResourceV10;
import com.cloudera.api.v10.ServicesResourceV10;
import com.cloudera.api.v8.RolesResourceV8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * controls Cloudera cluster services and their child roles:
 * Starts/Stop services, validate their status, etc...
 *
 */
public class ClouderaServiceImpl implements ClouderaService{

    private static Logger logger = LoggerFactory.getLogger(ClouderaServiceImpl.class);

    private RootResourceV10 apiRoot;
    private ServicesResourceV10 servicesRes;
    private final String serverHost;
    private final String cmAdminUser;
    private final String cmAdminPass;
    private final String clusterName;
    private Duration roleStartTimeout;
    private Duration roleStopTimeout;
    private ClouderaManagerClientBuilderFactoryHelper factoryHelper;

    public static class ClouderaManagerClientBuilderFactoryHelper {
        ClouderaManagerClientBuilder makeClouderaManagerClientBuilder(){
            return new ClouderaManagerClientBuilder();
        }
    }


    /**
     * C'tor
     */
    public ClouderaServiceImpl(String serverHost, String clusterName, String cmAdminUser, String cmAdminPass, Duration roleStartTimeout, Duration roleStopTimeout) {
        this.factoryHelper = new ClouderaManagerClientBuilderFactoryHelper();
        this.roleStartTimeout = roleStartTimeout;
        this.roleStopTimeout = roleStopTimeout;
        this.serverHost = serverHost;
        this.cmAdminUser = cmAdminUser;
        this.cmAdminPass = cmAdminPass;
        this.clusterName = clusterName;
    }

    void init() {
        logger.debug("initializing cloudera manager utils");
        apiRoot = factoryHelper.makeClouderaManagerClientBuilder().withHost(serverHost).withUsernamePassword(cmAdminUser, cmAdminPass).build().getRootV10();
        servicesRes = apiRoot.getClustersResource().getServicesResource(clusterName);
    }

    /**
     * Starts service
     * @param serviceName
     * @return
     */
    @Override
    public boolean start(String serviceName) {
        return changeServiceState(serviceName,ApiRoleState.STARTED,roleStartTimeout);
    }

    /**
     *
     * @param serviceName
     * @return True - if service installed at Cloudera cluster
     */
    @Override
    public boolean isInstalled(String serviceName) {
        ApiServiceList apiServices = servicesRes.readServices(DataView.SUMMARY);

        for (ApiService apiService : apiServices) {
            if (apiService.getName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param serviceName
     * @return
     */
    @Override
    public boolean isStarted(String serviceName) {
        return isAllServiceRolesAtState(serviceName,ApiRoleState.STARTED);
    }

    /**
     * @param serviceName
     * @return True - if all service roles are stopped, False - otherwise
     */
    @Override
    public boolean isStopped(String serviceName) {
        return isAllServiceRolesAtState(serviceName,ApiRoleState.STOPPED);
    }

    /**
     * Stops service
     * @param serviceName
     * @return True - if successfully stopped, False - otherwise
     */
    @Override
    public boolean stop(String serviceName) {
        return changeServiceState(serviceName,ApiRoleState.STOPPED,roleStopTimeout);
    }

    public boolean isAllServiceRolesAtState(String serviceName , ApiRoleState desiredState) {
        RolesResourceV8 rolesResource = servicesRes.getRolesResource(serviceName);
        for (ApiRole role : rolesResource.readRoles() ){
            ApiRoleState roleState = rolesResource.readRole(role.getName()).getRoleState();
            if (roleState != desiredState) {
                logger.warn("service {} is {}, desiredState is {}", serviceName, roleState,desiredState);
                return false;
            }
        }
        return true;
    }


    /**
     *
     * @param serviceName service to change state to
     * @param desiredState on what state do you like your service to be
     * @param desiredStateTimeout maximum time wait for all service roles to change a state
     * @return True - if service has change it's state, False - otherwise
     */
    public boolean changeServiceState(String serviceName, ApiRoleState desiredState, Duration desiredStateTimeout)
    {
        logger.info("changing service={} to state={}",serviceName,desiredState);
        ApiRoleNameList rolesFullNames = new ApiRoleNameList();
        String serviceNameLowerCase = serviceName.toLowerCase();
        RolesResourceV8 rolesResource = servicesRes.getRolesResource(serviceNameLowerCase);
        for (ApiRole currRole : rolesResource.readRoles()) {
            rolesFullNames.add(currRole.getName());
        }
        logger.info("service={} roles={}",serviceName, Arrays.toString(rolesFullNames.getRoleNames().toArray()));
        RoleCommandsResourceV10 roleCommandsResource = servicesRes.getRoleCommandsResource(serviceNameLowerCase);
        ApiBulkCommandList apiCommands = null;
        boolean arrivedDesiredState = false;
        switch (desiredState) {
            case STARTED:
                apiCommands = roleCommandsResource.startCommand(rolesFullNames);
                arrivedDesiredState = waitForDesiredState(ApiRoleState.STARTED, desiredStateTimeout, rolesResource, rolesFullNames);
                break;
            case STOPPED:
                apiCommands = roleCommandsResource.stopCommand(rolesFullNames);
                arrivedDesiredState = waitForDesiredState(ApiRoleState.STOPPED,desiredStateTimeout,rolesResource,rolesFullNames);
                break;
            default:
                String message = String.format("changing service=%s to state=%s is not supported",serviceName,desiredState);
                throw new UnsupportedOperationException(message);
        }
        if(apiCommands!=null)
        {
            List<String> apiCommandsErrors = apiCommands.getErrors();
            if(!apiCommandsErrors.isEmpty())
            {
                logger.error("errors during service={} state change into={} errors: {}",
                        serviceName,desiredState,Arrays.toString(apiCommandsErrors.toArray()));
            }
        }

        return arrivedDesiredState;
    }

    /**
     * waits for all roles to change into desired state until timeout has occurred
     * @param desiredState
     * @param desiredStateTimeout
     * @param rolesResource
     * @param rolesFullNames
     * @return True - if all service roles are at desired state. False - otherwise
     */
    public boolean waitForDesiredState(ApiRoleState desiredState,Duration desiredStateTimeout,RolesResourceV8 rolesResource,ApiRoleNameList rolesFullNames )
    {
        boolean allRolesInDesiredState = true;
        for (String currRole : rolesFullNames) {
            ApiRole apiRole = rolesResource.readRole(currRole);
            ApiRoleState currentRoleState = apiRole.getRoleState();
            Duration waitedDuration = Duration.ZERO;
            if (currentRoleState != desiredState) {
                while (waitedDuration.compareTo(desiredStateTimeout) < 0 && currentRoleState != desiredState) {
                    try {
                        Duration sleepDuration = Duration.ofSeconds(7);
                        Thread.sleep(sleepDuration.toMillis());
                        waitedDuration = waitedDuration.plus(sleepDuration);
                        currentRoleState = apiRole.getRoleState();
                    } catch (InterruptedException e) {
                        logger.error("sleep interrupted while waiting for role={} to change state to={}", currRole, desiredState, e);
                    }
                }
                apiRole = rolesResource.readRole(currRole);
                ApiHealthSummary currentRoleHealthSummary = apiRole.getHealthSummary();
                if (!currentRoleState.equals(desiredState)) {
                    logger.warn("timeout has occurred, role={} is at state={} should be at desired state={} after waiting={}",
                            currRole, currentRoleState, desiredState, waitedDuration);
                    allRolesInDesiredState = false;
                }
                if (currentRoleHealthSummary.equals(ApiHealthSummary.BAD)) {
                    logger.warn("role={} at BAD state", currRole);
                    allRolesInDesiredState = false;
                }
            }
        }

        return allRolesInDesiredState;
    }

    /**
     * used only for testing
     * @param servicesRes
     */
    void setServicesRes(ServicesResourceV10 servicesRes) {
        this.servicesRes = servicesRes;
    }
}