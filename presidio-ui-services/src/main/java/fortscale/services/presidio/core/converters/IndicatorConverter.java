package fortscale.services.presidio.core.converters;

import fortscale.domain.core.*;
import org.apache.commons.collections.CollectionUtils;
import presidio.output.client.model.Indicator;


import java.util.*;

/**
 * Created by shays on 10/09/2017.
 */
public class IndicatorConverter {

    DateConverter dateConverter = new DateConverter();



    public List<Evidence> convertIndicators(List<Indicator> indicators,AlertTimeframe alertTimeframe, String username){
        if (CollectionUtils.isEmpty(indicators)) {
            return Collections.emptyList();
        }

        List<Evidence> evidences = new ArrayList<>();
        indicators.forEach(indicator -> {
            evidences.add(convertIndicator(indicator,alertTimeframe,username));
        });

//        calculatePercantageTo100Percent(indicators);

        return  evidences;

    }
//
//    private void calculatePercantageTo100Percent(List<Indicator> indicators) {
//
//    }


    public Evidence convertIndicator(Indicator indicator, AlertTimeframe alertTimeframe,String userName){
        Evidence evidence = new Evidence();
        evidence.setAnomalyTypeFieldName(indicator.getName());
        evidence.setAnomalyValue(indicator.getAnomalyValue().toString());
        evidence.setEndDate(dateConverter.convertResponseTimeToUiTime(indicator.getEndDate()));
        evidence.setStartDate(dateConverter.convertResponseTimeToUiTime(indicator.getStartDate()));
        evidence.setEntityType(EntityType.User);
        evidence.setDataEntitiesIds(Arrays.asList(indicator.getSchema().toLowerCase()));
        evidence.setMockId(indicator.getId());
        evidence.setScore(intFromDouble(indicator.getScore()));
        evidence.setSeverity(Severity.Critical);
        evidence.setName(indicator.getName());
        evidence.setNumOfEvents(indicator.getEventsNum());
        evidence.setEntityName(userName);



        Double contributionScore = indicator.getScoreContribution();
        if (contributionScore!=null){
            contributionScore=contributionScore*100;
            evidence.setScoreContribution(intFromDouble(Math.floor(contributionScore)));
        }


        evidence.setEvidenceType(fetchEvidenceType(indicator));


        evidence.setTimeframe(AlertTimeframe.Daily.equals(alertTimeframe)?EvidenceTimeframe.Daily:EvidenceTimeframe.Hourly.Hourly);


        return evidence;
    }

    private Integer intFromDouble(Double d){
        return d!=null? d.intValue():null;
    }

    private EvidenceType fetchEvidenceType(Indicator indicator) {
        if (indicator.getType()==null){
            return null;
        }
        switch (indicator.getType()){
            case FEATURE_AGGREGATION: return  EvidenceType.AnomalyAggregatedEvent;
            case SCORE_AGGREGATION: return EvidenceType.AnomalySingleEvent;
            default: return  EvidenceType.StaticIndicator;
        }

    }
}
