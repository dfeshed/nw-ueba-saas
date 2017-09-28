package fortscale.ml.model.builder.gaussian;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.IModelBuilder;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.ConversionUtils.convertToDouble;

/**
 * Created by YaronDL on 9/24/2017.
 */
public class ContinuousMaxHistogramModelBuilder extends ContinuousHistogramModelBuilder {

    private int numOfMaxValuesSamples;

    public ContinuousMaxHistogramModelBuilder(ContinuousMaxHistogramModelBuilderConf builderConf){
        Assert.isTrue(builderConf.getNumOfMaxValuesSamples() > 0, "numOfMaxValuesSamples should be bigger than zero");
        this.numOfMaxValuesSamples = builderConf.getNumOfMaxValuesSamples();
    }


    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Double> histogram = castModelBuilderData(modelBuilderData).getHistogramMap();

        ContinuousDataModel continuousDataModel = buildContinuousDataModel(histogram);
        if(continuousDataModel.getN()>numOfMaxValuesSamples){
            ContinuousDataModel continuousDataModelOfMaxValues = buildContinuousDataModel(getMaxValuesHistogram(histogram));
            long N = continuousDataModel.getN();
            double mean = continuousDataModelOfMaxValues.getMean();
            double sd = continuousDataModel.getSd() > continuousDataModelOfMaxValues.getSd() ? continuousDataModel.getSd() : continuousDataModelOfMaxValues.getSd();
            double maxValue = continuousDataModelOfMaxValues.getMaxValue();
            continuousDataModel = new ContinuousDataModel();
            continuousDataModel.setParameters(N,mean,sd,maxValue);
        }

        return continuousDataModel;
    }

    private Map<String, Double> getMaxValuesHistogram(Map<String, Double> histogram){
        Comparator<Map.Entry<String, Double>> histogramKeyComparator = Comparator.comparingDouble(e -> convertToDouble(e.getKey()));
        Map<String, Double> sortedHistogram = histogram.entrySet().stream().sorted(histogramKeyComparator.reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Double> ret = new HashMap<>();
        double totalNumOfSamples = 0;
        for (Map.Entry<String, Double> entry : sortedHistogram.entrySet()) {
            double count = entry.getValue();
            if(totalNumOfSamples+count >= numOfMaxValuesSamples){
                ret.put(entry.getKey(),numOfMaxValuesSamples - totalNumOfSamples);
                break;
            } else{
                totalNumOfSamples += count;
                ret.put(entry.getKey(), count);
            }
        }

        return ret;
    }


}
