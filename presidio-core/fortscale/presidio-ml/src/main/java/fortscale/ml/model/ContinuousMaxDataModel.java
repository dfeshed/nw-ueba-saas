package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ContinuousMaxDataModel implements IContinuousDataModel,PartitionedDataModel {
    private Long numOfPartitions;
    private ContinuousDataModel continuousDataModel;
    private ContinuousDataModel continuousMaxDataModel;

    public ContinuousMaxDataModel() {
        this.numOfPartitions = 0L;
        this.continuousDataModel = null;
        this.continuousMaxDataModel = null;
    }

    /**
     *  @param continuousDataModel  model with all the data
     * @param continuousMaxDataModel model with max values
     * @param numOfPartitions number of time partitions the max data model relied upon
     */
    public ContinuousMaxDataModel(ContinuousDataModel continuousDataModel, ContinuousDataModel continuousMaxDataModel, long numOfPartitions) {
        this.continuousDataModel = continuousDataModel;
        this.continuousMaxDataModel = continuousMaxDataModel;
        this.numOfPartitions = numOfPartitions;
    }


    @Override
    public String toString() {
        return String.format("numOfPartitions=%d, <ContinuousMaxDataModel: N=%d, mean=%f, sd=%f, maxValue=%f > " +
                        "<ContinuousDataModel: N=%d, mean=%f, sd=%f, maxValue=%f>",
                numOfPartitions,continuousMaxDataModel.getN(), continuousMaxDataModel.getMean(), continuousMaxDataModel.getSd(), continuousMaxDataModel.getMaxValue(),
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

    @Override
    public long getNumOfPartitions() {
        return numOfPartitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContinuousMaxDataModel)) return false;
        ContinuousMaxDataModel that = (ContinuousMaxDataModel)o;
        return numOfPartitions.equals(that.numOfPartitions) &&
                (continuousDataModel == null ? that.continuousDataModel == null : continuousDataModel.equals(that.continuousDataModel)) &&
                (continuousMaxDataModel == null ? that.continuousMaxDataModel == null : continuousMaxDataModel.equals(that.continuousMaxDataModel));
    }

    @Override
    public int hashCode() {
        int result = numOfPartitions.hashCode();
        result = 31 * result + (continuousDataModel != null ? continuousDataModel.hashCode() : 0);
        result = 31 * result + (continuousMaxDataModel != null ? continuousMaxDataModel.hashCode() : 0);
        return result;
    }
}
