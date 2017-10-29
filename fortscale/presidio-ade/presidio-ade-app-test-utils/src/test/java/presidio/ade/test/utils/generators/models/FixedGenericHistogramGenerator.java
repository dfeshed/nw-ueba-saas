package presidio.ade.test.utils.generators.models;

import fortscale.common.util.GenericHistogram;
import presidio.data.domain.event.OperationType;
import presidio.data.generators.fileop.CyclicOperationTypeGenerator;

/**
 * Created by barak_schuster on 9/10/17.
 */
public class FixedGenericHistogramGenerator implements IGenericHistogramGenerator {

    private long numOfPartitions;
    private GenericHistogram genericHistogram;

    public FixedGenericHistogramGenerator() {
        this.genericHistogram = new GenericHistogram();
        CyclicOperationTypeGenerator fileOperationTypeGenerator = new CyclicOperationTypeGenerator();
        for (OperationType  operationType :
                fileOperationTypeGenerator.getValues()) {
                this.genericHistogram.add(operationType.toString(),3D);
        }
        this.numOfPartitions = 30;
        this.genericHistogram.setNumberOfPartitions(numOfPartitions);
    }

    public FixedGenericHistogramGenerator(GenericHistogram genericHistogram) {
        this.genericHistogram = genericHistogram;
        this.numOfPartitions = 30;
        this.genericHistogram.setNumberOfPartitions(numOfPartitions);
    }

    @Override
    public GenericHistogram getNext() {
        return genericHistogram;
    }
}
