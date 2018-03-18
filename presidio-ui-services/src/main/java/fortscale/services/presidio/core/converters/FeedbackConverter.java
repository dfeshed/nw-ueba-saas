package fortscale.services.presidio.core.converters;

import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.AlertStatus;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import presidio.output.client.model.Alert;
import presidio.output.client.model.AlertQuery;

import java.util.*;

/**
 * Created by shays on 10/09/2017.
 */
public class FeedbackConverter extends  EnumConverter<Alert.FeedbackEnum,AlertFeedback,AlertQuery.FeedbackEnum>{

    Logger logger = Logger.getLogger(this.getClass());
    static Map<Alert.FeedbackEnum,AlertFeedback> uiEnumToQueryEnum;

    static {
        uiEnumToQueryEnum = new HashMap<>();
        uiEnumToQueryEnum.put(Alert.FeedbackEnum.RISK,AlertFeedback.Approved);
        uiEnumToQueryEnum.put(Alert.FeedbackEnum.NOT_RISK,AlertFeedback.Rejected);
        uiEnumToQueryEnum.put(Alert.FeedbackEnum.NONE,AlertFeedback.None);
    }

    public FeedbackConverter() {
        super(uiEnumToQueryEnum, AlertQuery.FeedbackEnum.class);
    }

    public void updateFeedbackQuery(String feedbackArrayFilter, String statusArrayFilter, AlertQuery alertQuery) {
        Set<String> feedbackSet = splitAndTrim(feedbackArrayFilter, false);
        Set<String> statusesSet = splitAndTrim(statusArrayFilter, false);

        if (feedbackSet.isEmpty() && statusesSet.isEmpty()) {
            return;
        }

        if (!feedbackSet.isEmpty()) {
            List<AlertQuery.FeedbackEnum> queryList = super.convertUiFilterToQueryDto(feedbackArrayFilter);

            alertQuery.setFeedback(queryList);

        } else if (StringUtils.isNotEmpty(statusArrayFilter)){
            updateFromStatus(statusArrayFilter, alertQuery);
        }



    }

    private void updateFromStatus(String statusArrayFilter, AlertQuery alertQuery) {
        List<AlertQuery.FeedbackEnum> feedbackQueryEnumList = new ArrayList<>();
        List<AlertStatus> alertStatusUiEnumList = new ArrayList<>();

        String[] statusString = statusArrayFilter.split(",");
        for (int i=0; i<statusString.length;i++){
            AlertStatus alertStatus = AlertStatus.getByStringCaseInsensitive(statusString[i]);
            if (alertStatus==null){
                throw new RuntimeException("AlertStatus cannot be null");
            }
            alertStatusUiEnumList.add(alertStatus);
        }

        alertStatusUiEnumList.forEach(uiEnumFeedback->{
            switch (uiEnumFeedback){
                case Open: feedbackQueryEnumList.add(AlertQuery.FeedbackEnum.NONE); break;
                case Closed: feedbackQueryEnumList.add(AlertQuery.FeedbackEnum.NOT_RISK);
                    feedbackQueryEnumList.add(AlertQuery.FeedbackEnum.RISK);
                    break;
                default: {
                    logger.error("Try to convert impossible enum status value: "+uiEnumFeedback.name());
                }
            }

        });


        alertQuery.setFeedback(feedbackQueryEnumList);
    }


    public AlertStatus getStatus(presidio.output.client.model.Alert.FeedbackEnum feedbackEnum) {
        if (feedbackEnum == null){
            return AlertStatus.Open;
        }
        if (feedbackEnum.equals(presidio.output.client.model.Alert.FeedbackEnum.NONE)){
            return AlertStatus.Open;
        }
        return AlertStatus.Closed;
    }

}
