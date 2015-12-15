package fortscale.utils.cloudera;

/**
 * Created by Amir Keren on 23/09/2015.
 */

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.*;
import com.cloudera.api.v10.RootResourceV10;
import com.cloudera.api.v10.ServicesResourceV10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClouderaUtils {

    private static Logger logger = LoggerFactory.getLogger(ClouderaUtils.class);

    private static final int DEFAULT_TIMEOUT = 30;
    private static RootResourceV10 apiRoot;
    private static ServicesResourceV10 servicesRes;

    static class ClouderaManagerClientBuilderFactoryHelper {
        ClouderaManagerClientBuilder makeClouderaManagerClientBuilder(){
            return new ClouderaManagerClientBuilder();
        }
    }

    private ClouderaManagerClientBuilderFactoryHelper factoryHelper;

    public ClouderaUtils(String serverHost, String clusterName, String cmAdminUser, String cmAdminPass) {
        this(serverHost, clusterName, cmAdminUser, cmAdminPass, new ClouderaManagerClientBuilderFactoryHelper());
    }

    private ClouderaUtils(String serverHost, String clusterName, String cmAdminUser, String cmAdminPass, ClouderaManagerClientBuilderFactoryHelper factoryHelper) {
        this.factoryHelper = factoryHelper;
        init(serverHost, clusterName, cmAdminUser, cmAdminPass);

    }
    private void init(String serverHost, String clusterName, String cmAdminUser, String cmAdminPass) {
        logger.debug("initializing cloudera manager utils");
        apiRoot = factoryHelper.makeClouderaManagerClientBuilder().withHost(serverHost).withUsernamePassword(cmAdminUser, cmAdminPass).build().getRootV10();
        servicesRes = apiRoot.getClustersResource().getServicesResource(clusterName);
    }

    /**
     *
     * This method stops or starts service (serviceName variable), stopping or starting depend on isStop flag
     *
     * @param serviceName  the service to stop
     * @param isStop       whether to stop or to start the service roles. stop == true, start == false
     * @return
     */
    public boolean startOrStopService(String serviceName, boolean isStop) {
        ApiRoleState desiredRoleState;
        ApiRoleNameList rolesFullNames = new ApiRoleNameList();
        for (ApiRole currRole : servicesRes.getRolesResource(serviceName.toLowerCase()).readRoles()) {
            rolesFullNames.add(currRole.getName());
        }
        if (isStop) {
            logger.info("stopping {} tasks", rolesFullNames.size());
            servicesRes.getRoleCommandsResource(serviceName.toLowerCase()).stopCommand(rolesFullNames);
            desiredRoleState = ApiRoleState.STOPPED;
        } else {
            logger.info("starting {} tasks", rolesFullNames.size());
            servicesRes.getRoleCommandsResource(serviceName.toLowerCase()).startCommand(rolesFullNames);
            desiredRoleState = ApiRoleState.STARTED;
        }
        for (String role: rolesFullNames) {
            logger.debug(role);
        }
        int numberOfRolesInDesiredState = 0;
        int numberOfRolesInBadHealth = 0;
        int timer = 0;
        for (String currRoleName: rolesFullNames){
            while (timer < DEFAULT_TIMEOUT && servicesRes.getRolesResource(serviceName.toLowerCase()).
                    readRole(currRoleName).getRoleState() != desiredRoleState ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    logger.error("sleep interrupted {}", ex.getMessage());
                }
                if (timer % 10 == 0) {
                    logger.debug("waited {} seconds", timer);
                }
                timer++;
            }
            if (servicesRes.getRolesResource(serviceName.toLowerCase()).readRole(currRoleName).getRoleState() ==
                    desiredRoleState ) {
                numberOfRolesInDesiredState++;
                if (servicesRes.getRolesResource(serviceName.toLowerCase()).readRole(currRoleName).getHealthSummary() ==
                        ApiHealthSummary.BAD) {
                    numberOfRolesInBadHealth++;
                }
            }
            timer = 0;
        }
        if (numberOfRolesInDesiredState == rolesFullNames.size()) {
            if (isStop) {
                logger.info("roles have stopped successfully");
            } else {
                logger.info("roles have started successfully");
            }
            return true;
        }
        logger.info("Number of roles in state {} is {} out of {} roles", desiredRoleState, numberOfRolesInDesiredState,
                rolesFullNames.size());
        logger.warn("Number of roles in BAD health {} (out of {} roles)", numberOfRolesInBadHealth,
                rolesFullNames.size());
        return false;
    }

    /**
     *
     * Validates whether a service is started or stopped
     *
     * @param serviceName  service name to validate start/stop
     * @param isStop       boolean value - whether to check if service is started or stopped
     * @return
     */
    public boolean validateServiceStartedOrStopped(String serviceName, boolean isStop) {
        ApiRoleState validationValue = isStop ? ApiRoleState.STOPPED : ApiRoleState.STARTED;
        boolean serviceInstalled = false;
        logger.debug("checking if service {} is {}", serviceName, validationValue);
        ApiServiceList apiServices = servicesRes.readServices(DataView.SUMMARY);

        for (ApiService apiService : apiServices){
            if (apiService.getName().equals(serviceName)){
                serviceInstalled = true; break;
            }
        }

        if (serviceInstalled){ //do not try to stop a service that is not installed
            for (ApiRole role : servicesRes.getRolesResource(serviceName).readRoles() ){
                if (servicesRes.getRolesResource(serviceName).readRole(role.getName()).getRoleState() != validationValue) {
                    logger.warn("service {} is not {}", serviceName, validationValue);
                    return false;
                }
            }
        } else {
            logger.warn("Service {} is not installed", serviceName);
        }
        return true;
    }

}