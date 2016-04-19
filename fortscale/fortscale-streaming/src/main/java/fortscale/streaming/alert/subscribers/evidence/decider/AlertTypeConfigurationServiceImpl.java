package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.services.impl.ApplicationConfigurationHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by shays on 22/03/2016.
 * Contain the attributes required to decide about which indicator create the alert
 */
public class AlertTypeConfigurationServiceImpl {


    private Set<AlertTypeConfiguration> alertTypeConfigurations;
    private Map<String, AlertTypeConfiguration> evidenceTypeToAlertTypeConfigurations;

    @Autowired
    private ApplicationConfigurationHelper applicationConfigurationHelper;

    public AlertTypeConfigurationServiceImpl(Set<AlertTypeConfiguration> alertTypeConfigurations){
        this.alertTypeConfigurations = alertTypeConfigurations;

    }


    @PostConstruct
    public void init(){


        //Sync set with application configuration
        try {
            applicationConfigurationHelper.syncListOfObjectsWithConfiguration("alerts.congiruations", this,"alertTypeConfigurations",
                    AlertTypeConfiguration.class,
                    Arrays.asList(
                            new ImmutablePair("evidenceType","evidenceType"),
                            new ImmutablePair("alertTitle", "alertTitle"),
                            new ImmutablePair("namePriority", "namePriority"),
                            new ImmutablePair("scorePriority", "scorePriority")
                    ));


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


        //Init map
        evidenceTypeToAlertTypeConfigurations = new HashMap<>();
        for (AlertTypeConfiguration conf: alertTypeConfigurations){
            evidenceTypeToAlertTypeConfigurations.put(conf.getEvidenceType(), conf);
        }
    }

    public String getAlertNameByAnonalyType(String anomalyType){
        AlertTypeConfiguration conf = evidenceTypeToAlertTypeConfigurations.get(anomalyType);
        if (conf == null){
            return  null;
        } else {
            return conf.getAlertTitle();
        }
    }

    public Integer getPriority(String anomalyType,PriorityType type) {

        AlertTypeConfiguration conf = evidenceTypeToAlertTypeConfigurations.get(anomalyType);
        if (conf == null){
            throw new RuntimeException("Anomaly Type is not supported "+anomalyType);
        }
        switch (type){
            case NAME_PRIORITY:
                        return conf.getNamePriority();
            case SCORE_PRIORITY:
                        return conf.getScorePriority();
            default: throw new RuntimeException("Type is not supported");
        }
    }

    public boolean configurationExists(String anomalyType){
        return evidenceTypeToAlertTypeConfigurations.get(anomalyType) !=null;
    }


    public static enum PriorityType{
        NAME_PRIORITY,
        SCORE_PRIORITY
    }


    public static class AlertTypeConfiguration{
        private String evidenceType;
        private String alertTitle;

        private int namePriority;
        private int scorePriority;


        public AlertTypeConfiguration() {
        }

        public AlertTypeConfiguration(String evidenceType, String alertTitle, int namePriority, int scorePriority) {
            this.evidenceType = evidenceType;
            this.alertTitle = alertTitle;
            this.namePriority = namePriority;
            this.scorePriority = scorePriority;
        }

        public String getEvidenceType() {
            return evidenceType;
        }

        public void setEvidenceType(String evidenceType) {
            this.evidenceType = evidenceType;
        }

        public String getAlertTitle() {
            return alertTitle;
        }

        public void setAlertTitle(String alertTitle) {
            this.alertTitle = alertTitle;
        }

        public int getNamePriority() {
            return namePriority;
        }

        public void setNamePriority(int namePriority) {
            this.namePriority = namePriority;
        }

        public int getScorePriority() {
            return scorePriority;
        }

        public void setScorePriority(int scorePriority) {
            this.scorePriority = scorePriority;
        }
    }

    public Set<AlertTypeConfiguration> getAlertTypeConfigurations() {
        return alertTypeConfigurations;
    }

    public void setAlertTypeConfigurations(Set<AlertTypeConfiguration> alertTypeConfigurations) {
        this.alertTypeConfigurations = alertTypeConfigurations;
    }
}
