package com.rsa.netwitness.presidio.automation.utils.output;


import com.rsa.netwitness.presidio.automation.domain.output.ScoredEntityEventNormalizedUsernameDailyStoredData;
import com.rsa.netwitness.presidio.automation.domain.output.ScoredEntityEventNormalizedUsernameHourlyStoredData;

import java.util.List;

public class ExportDataFromMongoToJson {

    private List<ScoredEntityEventNormalizedUsernameDailyStoredData> scoredEntityDaily;
    private List<ScoredEntityEventNormalizedUsernameHourlyStoredData> scoredEntityHourly;

    public List<ScoredEntityEventNormalizedUsernameDailyStoredData> getScoredEntityDaily() {
        return scoredEntityDaily;
    }

    public void setScoredEntityDaily(List<ScoredEntityEventNormalizedUsernameDailyStoredData> scoredEntityDaily) {
        this.scoredEntityDaily = scoredEntityDaily;
    }

    public List<ScoredEntityEventNormalizedUsernameHourlyStoredData> getScoredEntityHourly() {
        return scoredEntityHourly;
    }

    public void setScoredEntityHourly(List<ScoredEntityEventNormalizedUsernameHourlyStoredData> scoredEntityHourly) {
        this.scoredEntityHourly = scoredEntityHourly;
    }
}
