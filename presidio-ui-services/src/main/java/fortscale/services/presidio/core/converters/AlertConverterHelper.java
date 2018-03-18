package fortscale.services.presidio.core.converters;

import fortscale.domain.core.*;
import fortscale.domain.core.Alert;
import fortscale.domain.dto.DateRange;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.client.model.*;

import java.util.*;
import java.util.stream.Collectors;
import fortscale.utils.logging.Logger;

/**
 * This remote converter include all the converstion for alert and alert query from the UI-BACKEND to Presidio-Output
 * and the other way around
 */

@Service
public class AlertConverterHelper {

    @Value("${alert.affect.duration.days}")
    private int alertEffectiveDurationInDays;


    Logger logger = Logger.getLogger(this.getClass());
    /**
     * Convert single reasponse been into single alert
     * @param alert
     * @return
     */

    final int MILI_SECONDS_IN_DAY=1000 * 60 * 60 * 24;

    private DateConverter dateConverter = new DateConverter();
    private PageConverter pageConverter = new PageConverter();
    private TagsConverter tagsConverter = new TagsConverter();
    private IndicatorTypesConverter indicatorTypesConverter = new IndicatorTypesConverter();


    private EnumConverter<presidio.output.client.model.Alert.SeverityEnum,Severity, AlertQuery.SeverityEnum> severityConverter;
    private EnumConverter<presidio.output.client.model.Alert.TimeframeEnum,AlertTimeframe,presidio.output.client.model.Alert.TimeframeEnum> timeframeConverter;
    private FeedbackConverter feedbackConverter = new FeedbackConverter();
    private StringConverter stringConverter = new StringConverter();
    private IndicatorConverter indicatorConverter = new IndicatorConverter();


    public AlertConverterHelper() {

        Map<presidio.output.client.model.Alert.SeverityEnum,Severity> queryEnumToUiEnum = new HashMap<>();
        queryEnumToUiEnum.put(presidio.output.client.model.Alert.SeverityEnum.CRITICAL,Severity.Critical);
        queryEnumToUiEnum.put(presidio.output.client.model.Alert.SeverityEnum.HIGH,Severity.High);
        queryEnumToUiEnum.put(presidio.output.client.model.Alert.SeverityEnum.MEDIUM,Severity.Medium);
        queryEnumToUiEnum.put(presidio.output.client.model.Alert.SeverityEnum.LOW ,Severity.Low);
        severityConverter = EnumConverter.createInstance(queryEnumToUiEnum,AlertQuery.SeverityEnum.class);

        Map<presidio.output.client.model.Alert.TimeframeEnum,AlertTimeframe> timeframesEnums=new HashMap<>();
        timeframesEnums.put(presidio.output.client.model.Alert.TimeframeEnum.DAILY,AlertTimeframe.Daily);
        timeframesEnums.put(presidio.output.client.model.Alert.TimeframeEnum.HOURLY,AlertTimeframe.Hourly);
        timeframeConverter=EnumConverter.createInstance(timeframesEnums, presidio.output.client.model.Alert.TimeframeEnum.class);


    }

//    public Alert convertResponseToUiDto(AlertSingleEntityResponseBean alert){
//        if (alert==null || alert.getData() == null){
//            return  null;
//        }
//        return convertResponseToUiDto(alert.getData());
//    }

    /**
     * Convert Alert from Presidio Output to UI-Backend alert
     * @param alert - from Presidio Output
     * @return Alert for the UI Backend
     */
    public  Alert convertResponseToUiDto(presidio.output.client.model.Alert alert){
        Alert uiAlert =  new Alert();

        uiAlert.setStartDate(dateConverter.convertResponseTimeToUiTime(alert.getStartDate()));
        uiAlert.setEndDate(dateConverter.convertResponseTimeToUiTime(alert.getEndDate()));
        uiAlert.setName(alert.getClassifiation()!=null && alert.getClassifiation().size()>0? alert.getClassifiation().get(0):null);
        uiAlert.setMockId(alert.getId());
        uiAlert.setSeverity(severityConverter.convertResponseToUiDto(alert.getSeverity()));
        uiAlert.setSeverityCode(toSeverityCode(uiAlert.getSeverity()));

        uiAlert.setScore(alert.getScore().intValue());
        uiAlert.setEvidenceSize(alert.getIndicatorsNum());
        uiAlert.setTimeframe(AlertTimeframe.Hourly);
        uiAlert.setEntityId(alert.getUserId());
        uiAlert.setEntityName(alert.getUsername());
        uiAlert.setEntityType(EntityType.User);
        uiAlert.setMockId(alert.getId());

        uiAlert.setEvidences(indicatorConverter.convertIndicators(alert.getIndicators(),uiAlert.getTimeframe(),uiAlert.getEntityName()));

//        uiAlert.setEvidences(indicatorConverter.convertIndicators(alert.getIndicators()));


        AlertFeedback feedback = feedbackConverter.convertResponseToUiDto(alert.getFeedback());
        if (feedback!=null) {
            uiAlert.setFeedback(feedback);
        } else {
            uiAlert.setFeedback(AlertFeedback.None);
        }
        uiAlert.setStatus(feedbackConverter.getStatus(alert.getFeedback()));
        uiAlert.setUserScoreContributionFlag(calculateIsAlertContributeToUserScore(alert.getFeedback(),uiAlert.getStartDate()));
        if(alert.getUserScoreContribution()!=null) {
            uiAlert.setUserScoreContribution(alert.getUserScoreContribution() == null ? 0D : alert.getUserScoreContribution().doubleValue());
        }
        return  uiAlert;
    }

    private boolean calculateIsAlertContributeToUserScore(presidio.output.client.model.Alert.FeedbackEnum feedback, long startDateMilisec) {
        if (feedback == null) {
            return true;
        }
/*        switch (feedback) {
            case APPROVED:
                return true;
            case REJECTED:
                return false;
            default:
                long milisecondsFromAlertStartTime = new Date().getTime() - startDateMilisec;
                if (milisecondsFromAlertStartTime <= MILI_SECONDS_IN_DAY * alertEffectiveDurationInDays) {
                    //Alert still affecting user score
                    return true;
                } else {
                    return false;
                }

        }*/
        return true;
    }

    private Integer toSeverityCode(Severity severity) {
        if (severity == null){
            return  null;
        }
        switch (severity) {
            case Critical:
                return 0;
            case High:
                return 1;
            case Medium:
                return 2;
            case Low:
                return 3;
        }

        throw new RuntimeException("Severity as unpossible value "+severity);
    }

    /**
     * Convert List of Alert Responses feom presidio output to list of alerts for the UI backend
     * @param alertsResponse - wrapper for List of Alert Responses
     * @return List of Alerts for UI Backend
     */
    public List<Alert> convertResponseToUiDto(AlertsWrapper alertsResponse){

        if (alertsResponse.getAlerts() == null){
            return null;
        }
        return alertsResponse.getAlerts().stream().map(alert->convertResponseToUiDto(alert)).collect(Collectors.toList());
    }

    /**
     * Convert alerts filter from the UI to presidio output AlertQuery
     * @param pageRequest
     * @param severityArray
     * @param statusArrayFilter
     * @param feedbackArrayFilter
     * @param dateRangeFilter
     * @param entityName
     * @param entityTags
     * @param entityId
     * @param indicatorTypes
     * @return
     */
    public AlertQuery convertUiFilterToQueryDto(PageRequest pageRequest, String severityArray, String statusArrayFilter,
                                      String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, String entityTags, String entityId,
                                      Set<String> indicatorTypes, boolean expand) {

        AlertQuery alertQuery = new AlertQuery();


        alertQuery.setPageNumber(pageConverter.convertUiFilterToQueryDtoPageNumber(pageRequest));
        alertQuery.setPageSize(pageConverter.convertUiFilterToQueryDtoPageSize(pageRequest));
        alertQuery.setSortFieldNames(pageConverter.convertUiFilterToQueryDtoAlertSortFields(pageRequest));
        if (CollectionUtils.isNotEmpty(alertQuery.getSortFieldNames())) {
            alertQuery.setSortDirection(pageConverter.convertUiFilterToQueryDtoSortDirectionForAlert(pageRequest));
        }
        alertQuery.setSeverity(severityConverter.convertUiFilterToQueryDto(severityArray));
        dateConverter.updateDateRangeParamsOnQuery(dateRangeFilter, alertQuery);
        feedbackConverter.updateFeedbackQuery(feedbackArrayFilter,statusArrayFilter,alertQuery);


        List<String> tags = tagsConverter.convertUiFilterToQueryDto(entityTags,null);
        alertQuery.setTags(tags);

        List<String> userNames = stringConverter.convertUiFilterToQueryDto(entityName);
        alertQuery.setUserName(userNames);

        List<String> userIds = stringConverter.convertUiFilterToQueryDto(entityId);
        alertQuery.setUsersId(userIds);

        alertQuery.setIndicatorsName(indicatorTypesConverter.convertUiFilterToQueryDto(indicatorTypes));


        alertQuery.setExpand(expand);

        return alertQuery;
    }



    public FeedbackConverter getFeedbackConverter(){
        return this.feedbackConverter;
    }







}
