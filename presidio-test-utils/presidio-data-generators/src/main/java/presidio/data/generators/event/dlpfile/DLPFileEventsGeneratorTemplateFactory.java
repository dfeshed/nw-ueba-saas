package presidio.data.generators.event.dlpfile;

import presidio.data.generators.dlpfileop.DLPFileOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.fileentity.SimplePathGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.io.InputStream;

public class DLPFileEventsGeneratorTemplateFactory {
    public DLPFileEventsGenerator getDLPFileEventSingleUserGenerator(String username) throws GeneratorException {

        DLPFileEventsGenerator generator = new DLPFileEventsGenerator();
        generator.setUserGenerator(new SingleUserGenerator(username)); // Handles: username, first_name, last_name
        generator.setSourceMachineGenerator(new QuestADMachineGenerator()); // Handles: hostname, source_ip
        generator.setEventIDGenerator(new EntityEventIDFixedPrefixGenerator(username));

        return generator;
    }

    public DLPFileOperationGenerator getDLPFileOperationCustomPathsGenerator() throws GeneratorException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream srcPathsStream = classLoader.getResourceAsStream("data/FilesList1.txt");
        InputStream destPathsStream = classLoader.getResourceAsStream("data/FilesList2.txt");

        DLPFileOperationGenerator generator = new DLPFileOperationGenerator();
        generator.setSourcePathGenerator(new SimplePathGenerator(srcPathsStream));
        generator.setDestPathGenerator(new SimplePathGenerator(destPathsStream));

        return generator;
    }

    public SimplePathGenerator getCustomPathsGenerator() throws GeneratorException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream srcPathsStream = classLoader.getResourceAsStream("data/FilesList1.txt");
        SimplePathGenerator generator = new SimplePathGenerator(srcPathsStream);

        return generator;
    }

}

