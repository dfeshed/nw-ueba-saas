package presidio.data.generators.common.precentage;

import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.domain.event.file.FILE_OPERATION_RESULT;
import presidio.data.generators.common.GeneratorException;

public class OperationResultPercentageGenerator extends AbstractStringPercentageGenerator implements IStringGenerator {

    // 100% "Success"
    public OperationResultPercentageGenerator() throws GeneratorException {
        super(FILE_OPERATION_RESULT.SUCCESS.value);
    }

    public OperationResultPercentageGenerator(String[] options, int[] percents) throws GeneratorException {
        super(options, percents);
    }
}