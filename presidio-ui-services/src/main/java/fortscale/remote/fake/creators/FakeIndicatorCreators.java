package fortscale.remote.fake.creators;

import presidio.output.client.model.Indicator;
import presidio.output.client.model.IndicatorsWrapper;

public class FakeIndicatorCreators {

    private FakeCreatorUtils fakeCreatorUtils;

    public FakeIndicatorCreators(FakeCreatorUtils fakeCreatorUtils) {
        this.fakeCreatorUtils = fakeCreatorUtils;
    }

    public IndicatorsWrapper getIndicators(int numberOfIndicators, String startTime, String endTime){

        IndicatorsWrapper indicatorsWrapper = new IndicatorsWrapper();

        for (int i=0; i<numberOfIndicators;i++) {



                Indicator indicator = new Indicator();
                indicator.setAnomalyValue(5);
                indicator.startDate(fakeCreatorUtils.timeStringToEpochBig(startTime));
                indicator.setEndDate(fakeCreatorUtils.timeStringToEpochBig(endTime));
                indicator.setName("admin_changed_his_own_password");
                indicator.setSchema("active_directory");
                indicator.setScore(5D);
                indicator.setScoreContribution(0.8D);
                indicator.eventsNum(20);
                indicator.setType(Indicator.TypeEnum.STATIC_INDICATOR);
                indicator.setId("bla"+i);


                indicatorsWrapper.addIndicatorsItem(indicator);
            }

        indicatorsWrapper.setTotal(numberOfIndicators);
        return indicatorsWrapper;



    }
}
