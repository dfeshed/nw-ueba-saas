package presidio.ade.test.utils.generators.models;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.TimeModel;
import fortscale.ml.model.builder.TimeModelBuilder;
import fortscale.ml.model.builder.TimeModelBuilderConf;

/**
 * Created by barak_schuster on 9/11/17.
 */
public class TimeModelGenerator implements IModelGenerator<TimeModel> {
    private TimeModelBuilder timeModelBuilder;
    private TimeModelBuilderConf timeModelBuilderConf;
    private IGenericHistogramGenerator genericHistogramGenerator;

    public TimeModelGenerator(TimeModelBuilderConf conf) {
        timeModelBuilderConf = conf;
        timeModelBuilder = new TimeModelBuilder(conf);
        GenericHistogram genericHistogram = new GenericHistogram();
        for (int i = 0; i < 40; i++) {
            genericHistogram.add(i,0D);
        }
        for (int i = 40; i < 90; i++) {
            genericHistogram.add(i,2D);
        }
        for (int i = 90; i < 144; i++) {
            genericHistogram.add(i,0.3D);
        }
        genericHistogramGenerator = new FixedGenericHistogramGenerator(genericHistogram);
    }

    @Override
    public TimeModel getNext() {
        return (TimeModel) timeModelBuilder.build(genericHistogramGenerator.getNext());
    }

    public void setTimeModelBuilder(TimeModelBuilder timeModelBuilder) {
        this.timeModelBuilder = timeModelBuilder;
    }

    public void setTimeModelBuilderConf(TimeModelBuilderConf timeModelBuilderConf) {
        this.timeModelBuilderConf = timeModelBuilderConf;
    }

    public void setGenericHistogramGenerator(IGenericHistogramGenerator genericHistogramGenerator) {
        this.genericHistogramGenerator = genericHistogramGenerator;
    }
}
