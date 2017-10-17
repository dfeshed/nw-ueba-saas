package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ContinuousMaxDataModel implements IContinuousDataModel {
    private ContinuousDataModel continuousDataModel;
    private ContinuousDataModel continuousMaxDataModel;

    /**
     *
     * @param continuousDataModel  model will all data
     * @param continuousMaxDataModel model with max values
     */
    public ContinuousMaxDataModel(ContinuousDataModel continuousDataModel, ContinuousDataModel continuousMaxDataModel) {
        this.continuousDataModel = continuousDataModel;
        this.continuousMaxDataModel = continuousMaxDataModel;
    }


    @Override
    public String toString() {
        return String.format("<ContinuousMaxDataModel: N=%d, mean=%f, sd=%f, maxValue=%f> " +
                        "<ContinuousDataModel: N=%d, mean=%f, sd=%f, maxValue=%f>",
                continuousMaxDataModel.getN(), continuousMaxDataModel.getMean(), continuousMaxDataModel.getSd(), continuousMaxDataModel.getMaxValue(),
                continuousDataModel.getN(), continuousDataModel.getMean(), continuousDataModel.getSd(), continuousDataModel.getMaxValue()
        );
    }

    @Override
    public long getNumOfSamples() {
        return continuousDataModel.getN();
    }

    @Override
    public long getN() {
        return continuousDataModel.getN();
    }

    @Override
    public double getMean() {
        return continuousMaxDataModel.getMean();
    }

    @Override
    public double getSd() {
        return Math.max(continuousMaxDataModel.getSd(), continuousDataModel.getSd());
    }

    @Override
    public double getMaxValue() {
        return continuousMaxDataModel.getMaxValue();
    }
}
