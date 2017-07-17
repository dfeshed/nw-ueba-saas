package presidio.data.generators.file;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.precentage.AbstractStringPercentageGenerator;

public class DriveTypePercentageGenerator extends AbstractStringPercentageGenerator implements IStringGenerator {


    // ALL will be "Fixed"
    public DriveTypePercentageGenerator() throws GeneratorException {
        super("Fixed");
    }

    public DriveTypePercentageGenerator(String[] options, int[] percents) throws GeneratorException {
        super(options, percents);
    }

}
