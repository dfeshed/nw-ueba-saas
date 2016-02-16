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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClouderaUtils {

    private static Logger logger = LoggerFactory.getLogger(ClouderaUtils.class);

    private static final int DEFAULT_STOP_TIMEOUT_PER_TASK = 120;
    private static final int DEFAULT_START_TIMEOUT_PER_TASK = 240;
    private static final int TASK_STATUS_POLLING_INTERVAL = 1000; // in milliseconds
    private static final int LOG_TASK_WAITING_INTERVAL = 10; // in seconds
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

    private ClouderaUtils(String serverHost, String clusterName, String cmAdminUser, String cmAdminPass,
            ClouderaManagerClientBuilderFactoryHelper factoryHelper) {
        this.factoryHelper = factoryHelper;
        init(serverHost, clusterName, cmAdminUser, cmAdminPass);

    }
    private void init(String serverHost, String clusterName, String cmAdminUser, String cmAdminPass) {
        logger.debug("initializing cloudera manager utils");
        apiRoot = factoryHelper.makeClouderaManagerClientBuilder().withHost(serverHost).
                withUsernamePassword(cmAdminUser, cmAdminPass).build().getRootV10();
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
     * this method stops or starts tasks in a cloudera service (streamingServiceName variable),
     * stopping or starting depend on what you put in isStop variable
     * is you wish to stop or start all tasks just put true to isStopStartAll otherwise give
     * tasks list to take care of in UPPERCASE!
     * @param streamingServiceName the service to stop
     * @param tasks tasks of the service to stop. E.g., Samza task in FSSTREAMING
     * @param isStop whether to stop or to start the service tasks. stop ==true, start == false
     * @param isStopStartAll true when we want to stop the service with all its tasks
     *
     * @return true if all tasks switched to the desired state, false otherwise
     */
    public boolean startStopTask(String streamingServiceName, List<String> tasks, boolean isStop,
            boolean isStopStartAll) {
        ApiRoleState desiredTaskState;
        ApiRoleNameList tasksFullNames = new ApiRoleNameList();
        for (ApiRole currRole : servicesRes.getRolesResource(streamingServiceName.toLowerCase()).readRoles()){
            if (isStopStartAll || tasks !=null && (tasks.contains(currRole.getType()))){
                tasksFullNames.add(currRole.getName());
            }
        }
        if (isStop){
            servicesRes.getRoleCommandsResource(streamingServiceName.toLowerCase()).stopCommand(tasksFullNames);
            logger.info("stopping the following tasks: ");
            desiredTaskState = ApiRoleState.STOPPED;
        }
        else{
            servicesRes.getRoleCommandsResource(streamingServiceName.toLowerCase()).restartCommand(tasksFullNames);
            logger.info("starting the following tasks: ");
            desiredTaskState = ApiRoleState.STARTED;
        }
        for (String task: tasksFullNames){
            logger.info(task);
        }
        int numberOfTasksInDesiredState=0;
        int numberOfTasksInBadHealth=0;
        int waitedTimeInSeconds = 0;
        int defaultTimeoutPerTask = (isStop ? DEFAULT_STOP_TIMEOUT_PER_TASK : DEFAULT_START_TIMEOUT_PER_TASK);
        Map<String, ApiRoleState> taskToStateMap = new HashMap<>();
        Map<String, ApiHealthSummary> taskToHealthMap = new HashMap<>();
        for (String currRoleName: tasksFullNames){
            ApiRoleState currRoleState = retrieveRoleState(streamingServiceName, currRoleName);
            ApiHealthSummary currRoleHealthSummary = retrieveRoleHealth(streamingServiceName, currRoleName);
            while (waitedTimeInSeconds < defaultTimeoutPerTask && currRoleState != desiredTaskState ){
                try {
                    Thread.sleep(TASK_STATUS_POLLING_INTERVAL);
                } catch (InterruptedException e) {
                    logger.error("sleep interrupted", e.getMessage());
                }
                if (waitedTimeInSeconds % LOG_TASK_WAITING_INTERVAL == 0){
                    logger.info("waited " + waitedTimeInSeconds + " Seconds");
                }
                waitedTimeInSeconds++;
                currRoleState = retrieveRoleState(streamingServiceName, currRoleName);
                currRoleHealthSummary = retrieveRoleHealth(streamingServiceName, currRoleName);
            }
            taskToStateMap.put(currRoleName, currRoleState);
            taskToHealthMap.put(currRoleName, currRoleHealthSummary);
            if (currRoleState == desiredTaskState ){
                numberOfTasksInDesiredState++;
                if (ApiHealthSummary.BAD == currRoleHealthSummary) {
                    numberOfTasksInBadHealth++;
                }
            }
            waitedTimeInSeconds = 0;
        }
        if (numberOfTasksInDesiredState == tasksFullNames.size() && numberOfTasksInBadHealth == 0) {
            logger.info("All tasks are in desired state. Number of tasks in state {} is {} out of {} tasks",
                    desiredTaskState, numberOfTasksInDesiredState, tasksFullNames.size());
            return true;
        } else {
            if (numberOfTasksInDesiredState < tasksFullNames.size()) {
                logger.error("Some of the tasks are not in desired state. Number of tasks in state {} is {} out of {} tasks",
                        desiredTaskState, numberOfTasksInDesiredState, tasksFullNames.size());
                printTasksState(desiredTaskState, taskToStateMap);
            }
            else { // ==> numberOfTasksInBadHealth > 0
                logger.error("Number of tasks in BAD health {} (out of {} tasks)", numberOfTasksInBadHealth,
                        tasksFullNames.size());
                printTasksHealth(taskToHealthMap);
            }
            return false;
        }
    }

	/**
     *
     * This method checks on a specific role's health
     *
     * @param serviceName
     * @param currRoleName
     * @return
     */
    private ApiHealthSummary retrieveRoleHealth(String serviceName, String currRoleName) {
        return servicesRes.getRolesResource(serviceName.toLowerCase()).readRole(currRoleName).getHealthSummary();
    }

	/**
     *
     * This method checks on a specific role's state
     *
     * @param serviceName
     * @param currRoleName
     * @return
     */
    private ApiRoleState retrieveRoleState(String serviceName, String currRoleName) {
        return servicesRes.getRolesResource(serviceName.toLowerCase()).readRole(currRoleName).getRoleState();
    }

	/**
     *
     * This method prints the tasks current health
     *
     * @param taskToHealthMap
     */
    private void printTasksHealth(Map<String, ApiHealthSummary> taskToHealthMap) {
        for (Map.Entry<String, ApiHealthSummary> taskToHealthEntry : taskToHealthMap.entrySet()) {
            String taskName = taskToHealthEntry.getKey();
            ApiHealthSummary taskHealth = taskToHealthEntry.getValue();
            if (ApiHealthSummary.BAD != taskHealth) {
                logger.info("Task " + taskName + " in " + taskHealth + " health");
            }
            else {
                logger.error("Task " + taskName + " in " + taskHealth + " health");
            }
        }
    }

	/**
     *
     * This method is a helper method to print to log
     *
     * @param desiredRoleState
     * @param taskToStateMap
     */
    private void printTasksState(ApiRoleState desiredRoleState, Map<String, ApiRoleState> taskToStateMap) {
        for (Map.Entry<String, ApiRoleState> taskToStateEntry : taskToStateMap.entrySet()) {
            String taskName = taskToStateEntry.getKey();
            ApiRoleState taskState = taskToStateEntry.getValue();
            if (desiredRoleState == taskState) {
                logger.info("Task " + taskName + " in state " + taskState);
            }
            else {
                logger.error("Task " + taskName + " in state " + taskState);
            }
        }
    }

    /**
     * validates whether a service role(s) is started or stopped
     * @param serviceName
     * @param roles the list of roles to check
     * @param isStop boolean value - whether to check if service is started or stopped
     * @param isStopStartAll boolean flag that tells to test all roles of a service
     * @return
     */
    public boolean validateServiceRoles(String serviceName, List<String> roles, boolean isStop, boolean isStopStartAll){
        ApiRoleState validationValue = isStop ? ApiRoleState.STOPPED : ApiRoleState.STARTED;
        for (ApiRole role : servicesRes.getRolesResource(serviceName).readRoles() ){
            if (roles !=null && (roles.contains(role.getType())) || isStopStartAll) {
                if (servicesRes.getRolesResource(serviceName).readRole(role.getName()).getRoleState() !=
                        validationValue) {
                    return false;
                }
            }
        }
        return true;
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
                if (servicesRes.getRolesResource(serviceName).readRole(role.getName()).getRoleState() !=
                        validationValue) {
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