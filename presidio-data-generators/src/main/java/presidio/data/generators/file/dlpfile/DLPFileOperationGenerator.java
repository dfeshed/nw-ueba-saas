package presidio.data.generators.file.dlpfile;

import presidio.data.generators.domain.event.file.dlpfile.DLPFileOperation;
import presidio.data.generators.file.FileNameDefaultExtGenerator;
import presidio.data.generators.file.FileSizeIncrementalGenerator;
import presidio.data.generators.file.OperationTypeCyclicGenerator;
import presidio.data.generators.file.SimplePathGenerator;

public class DLPFileOperationGenerator implements IDLPFileOperationGenerator {

    private FileNameDefaultExtGenerator sourceFileNameGenerator;
    private FileNameDefaultExtGenerator destFileNameGenerator;
    private SimplePathGenerator sourcePathGenerator;
    private SimplePathGenerator destPathGenerator;
    private FileSizeIncrementalGenerator fileSizeGenerator;
    private OperationTypeCyclicGenerator eventTypeGenerator;

    public DLPFileOperationGenerator()  {
        sourceFileNameGenerator = new FileNameDefaultExtGenerator();
        destFileNameGenerator = new FileNameDefaultExtGenerator();
        sourcePathGenerator = new SimplePathGenerator();
        destPathGenerator = new SimplePathGenerator();
        fileSizeGenerator = new FileSizeIncrementalGenerator();
        eventTypeGenerator = new OperationTypeCyclicGenerator();
    }

    public DLPFileOperation getNext(){
        return new DLPFileOperation((String) sourceFileNameGenerator.getNext(), (String) destFileNameGenerator.getNext(),
                (String) sourcePathGenerator.getNext(), (String) destPathGenerator.getNext(), fileSizeGenerator.getNext(),
                (String) eventTypeGenerator.getNext());
    }

    public void setSourceFileNameGenerator(FileNameDefaultExtGenerator sourceFileNameGenerator) {
        this.sourceFileNameGenerator = sourceFileNameGenerator;
    }

    public void setDestFileNameGenerator(FileNameDefaultExtGenerator destFileNameGenerator) {
        this.destFileNameGenerator = destFileNameGenerator;
    }

    public void setSourcePathGenerator(SimplePathGenerator sourcePathGenerator) {
        this.sourcePathGenerator = sourcePathGenerator;
    }

    public void setDestPathGenerator(SimplePathGenerator destPathGenerator) {
        this.destPathGenerator = destPathGenerator;
    }

    public void setFileSizeGenerator(FileSizeIncrementalGenerator fileSizeGenerator) {
        this.fileSizeGenerator = fileSizeGenerator;
    }

    public void setEventTypeGenerator(OperationTypeCyclicGenerator eventTypeGenerator) {
        this.eventTypeGenerator = eventTypeGenerator;
    }

    public FileNameDefaultExtGenerator getSourceFileNameGenerator() {
        return sourceFileNameGenerator;
    }

    public FileNameDefaultExtGenerator getDestFileNameGenerator() {
        return destFileNameGenerator;
    }

    public SimplePathGenerator getSourcePathGenerator() {
        return sourcePathGenerator;
    }

    public SimplePathGenerator getDestPathGenerator() {
        return destPathGenerator;
    }

    public FileSizeIncrementalGenerator getFileSizeGenerator() {
        return fileSizeGenerator;
    }

    public OperationTypeCyclicGenerator getEventTypeGenerator() {
        return eventTypeGenerator;
    }
}
