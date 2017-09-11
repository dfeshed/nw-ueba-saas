package presidio.ade.test.utils.generators.models;

import fortscale.common.util.GenericHistogram;
import presidio.data.domain.event.OperationType;
import presidio.data.generators.fileop.CyclicFileOperationTypeGenerator;

/**
 * Created by barak_schuster on 9/10/17.
 */
public class FixedGenericHistogramGenerator implements IGenericHistogramGenerator {

    private GenericHistogram genericHistogram;

    public FixedGenericHistogramGenerator() {
        this.genericHistogram = new GenericHistogram();
        CyclicFileOperationTypeGenerator fileOperationTypeGenerator = new CyclicFileOperationTypeGenerator();
        for (OperationType  operationType :
                fileOperationTypeGenerator.getValues()) {
                this.genericHistogram.add(operationType.toString(),3D);
        }
    }

    public FixedGenericHistogramGenerator(GenericHistogram genericHistogram) {
        this.genericHistogram = genericHistogram;
    }

    @Override
    public GenericHistogram getNext() {
        return genericHistogram;
    }
}
