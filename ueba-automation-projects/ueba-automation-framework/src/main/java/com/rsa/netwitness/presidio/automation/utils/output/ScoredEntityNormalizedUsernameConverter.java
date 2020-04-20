package com.rsa.netwitness.presidio.automation.utils.output;


import com.rsa.netwitness.presidio.automation.domain.output.ScoredEntityEventNormalizedUsernameDailyStoredData;
import com.rsa.netwitness.presidio.automation.domain.output.ScoredEntityEventNormalizedUsernameStoredRecored;

import java.util.ArrayList;
import java.util.List;

public class ScoredEntityNormalizedUsernameConverter {

    public List<ScoredEntityEventNormalizedUsernameStoredRecored> convertDaily(List<ScoredEntityEventNormalizedUsernameDailyStoredData> scoredEntity) {

        List<ScoredEntityEventNormalizedUsernameStoredRecored> recoreds = new ArrayList<ScoredEntityEventNormalizedUsernameStoredRecored>();

        for(ScoredEntityEventNormalizedUsernameDailyStoredData entity : scoredEntity){
            ScoredEntityEventNormalizedUsernameStoredRecored record = new ScoredEntityEventNormalizedUsernameStoredRecored();
            record.setStart_time_unix(entity.getStart_time_unix());
            record.setEntity_event_value(entity.getEntity_event_value());
            record.setScore(entity.getScore());
            record.setFeature_score(entity.getFeature_score());
            record.setUnreduced_score(entity.getUnreduced_score());
            record.setContext(entity.getContext());
            record.setContextId(entity.getContextId());
            record.setEnd_time_unix(entity.getEnd_time_unix());
            record.setCreation_time(entity.getCreation_time());
            record.setCreation_epochtime(entity.getCreation_epochtime());
            record.setEntity_event_type(entity.getEntity_event_type());
            record.setDate_time_unix(entity.getDate_time_unix());
            record.setAggregated_feature_events(entity.getAggregated_feature_events());
            record.setEntity_event_name(entity.getEntity_event_name());

            recoreds.add(record);
        }

        return recoreds;
    }
}
