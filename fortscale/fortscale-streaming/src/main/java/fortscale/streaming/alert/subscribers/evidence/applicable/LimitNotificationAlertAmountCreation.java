package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EvidenceType;
import fortscale.services.ApplicationConfigurationService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shays on 16/03/2016.
 * Filter evidence which its matching alert already got to maximum allowed in the time unit.
 * For example, if we already 15 geo hopping alerts in the daily time unit 28/March/2016 - 29/March/2016
 * We don't like to open new alerts of geo hopping type, so we filter them here.
 */
public class LimitNotificationAlertAmountCreation implements PreAlertDeciderFilter {



    public static String MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY_KEY ="maxAmountOfNotificationsAlertinDay";
    public static String MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_HOUR_KEY ="maxAmountOfNotificationsAlertinHour";
    public static int DEFAULT_MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY =10;
    public static int DEFAULT_MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_ALERT_IN_HOUR =10;
    public static int milisecInHour = 1000* 60 * 60;


    @Autowired
    private DeciderConfiguration deciderConfiguration;

    @Autowired
    private AlertTypesHisotryCache alertTypesHisotryCache;

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;



    /**
     * Execute the filter and decide if the alert type associated to this evidence already apeard to many times in the time unit
     * @param evidencesOrEntityEvents
     * @param alertWindowStartDate
     * @param alertWindowEndTime
     * @return
     */
    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long alertWindowStartDate, Long alertWindowEndTime){

        String title = deciderConfiguration.getAlertNameByAnonalyType(evidencesOrEntityEvents.getAnomalyTypeFieldName());
        long previousAmountOfTimes = alertTypesHisotryCache.getOccurances(title, alertWindowStartDate, alertWindowEndTime);

        int maxAmountOfSameAlert = getMaxAmountOfNotifications(alertWindowStartDate, alertWindowEndTime);
        return maxAmountOfSameAlert>=previousAmountOfTimes;
    }


    //Test if the evidence is notification.
    //Other type of evidences filter in the smart mechanism
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType){
        return EvidenceType.Notification.equals(evidenceType);
    }


    /**
     * Get the maximum amout of notification for the time unit hourly / daily
     * The value taken from configation. If not apear in the configuration it has default value,
     * and set it to configuration
     * @param alertWindowStartDate
     * @param alertWindowEndTime
     * @return
     */
    private int getMaxAmountOfNotifications(Long alertWindowStartDate, Long alertWindowEndTime){

        if (alertWindowEndTime-alertWindowStartDate > milisecInHour) { // Daily
            return getApplicationConfigurarionOrUpdate(MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY_KEY,
                    DEFAULT_MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_ALERT_IN_HOUR);
        } else { //Hourly
            return getApplicationConfigurarionOrUpdate(MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY_KEY,
                    DEFAULT_MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY);
        }

    }

    private int getApplicationConfigurarionOrUpdate(String key, int defaultValue){
        ApplicationConfiguration applicationConfiguration = applicationConfigurationService.getApplicationConfigurationByKey(key);
        String value = applicationConfiguration.getValue();
        if (value == null){
            value = Integer.toString(defaultValue);
            applicationConfigurationService.insertConfigItem(key, value);
        }
        return Integer.parseInt(value);
    }



}
