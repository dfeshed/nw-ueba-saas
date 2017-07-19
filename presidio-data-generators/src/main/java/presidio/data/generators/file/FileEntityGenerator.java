package presidio.data.generators.file;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.domain.event.file.FileEntity;

public class FileEntityGenerator implements IFileEntityGenerator {

    FileNameDefaultExtGenerator fileNameGenerator;
    SimplePathGenerator filePathGenerator;
    FileSizeIncrementalGenerator fileSizeGenerator;
    BooleanPercentageGenerator isDriveSharedGenerator;
    BooleanPercentageGenerator isDirectoryGenerator;

    public FileEntityGenerator() throws GeneratorException {
        fileNameGenerator = new FileNameDefaultExtGenerator();
        filePathGenerator = new SimplePathGenerator();
        fileSizeGenerator = new FileSizeIncrementalGenerator();

        isDriveSharedGenerator = new BooleanPercentageGenerator(20); // 20% shared
        isDirectoryGenerator = new BooleanPercentageGenerator(10);    // 10% directories
    }

    public FileEntity getNext(){
        boolean isDirectory = getIsDirectoryGenerator().getNext();

        // alter file name and size in case if generating folder domain
        String fileName = (String) getFileNameGenerator().getNext();
        String filePath = (String) getFilePathGenerator().getNext();
        long fileSize = getFileSizeGenerator().getNext() * (isDirectory ? 10:1); // make directory size larger
        boolean isDriveShared = getIsDriveSharedGenerator().getNext();

        return new FileEntity(fileName, filePath ,fileSize, isDriveShared, isDirectory);
    }

    public BooleanPercentageGenerator getIsDriveSharedGenerator() {
        return isDriveSharedGenerator;
    }

    public void setIsDriveSharedGenerator(BooleanPercentageGenerator isDriveSharedGenerator) {
        this.isDriveSharedGenerator = isDriveSharedGenerator;
    }

    public FileNameDefaultExtGenerator getFileNameGenerator() {
        return fileNameGenerator;
    }

    public void setFileNameGenerator(FileNameDefaultExtGenerator fileNameGenerator) {
        this.fileNameGenerator = fileNameGenerator;
    }

    public SimplePathGenerator getFilePathGenerator() {
        return filePathGenerator;
    }

    public void setFilePathGenerator(SimplePathGenerator filePathGenerator) {
        this.filePathGenerator = filePathGenerator;
    }

    public FileSizeIncrementalGenerator getFileSizeGenerator() {
        return fileSizeGenerator;
    }

    public void setFileSizeGenerator(FileSizeIncrementalGenerator fileSizeGenerator) {
        this.fileSizeGenerator = fileSizeGenerator;
    }

    public BooleanPercentageGenerator getIsDirectoryGenerator() {
        return isDirectoryGenerator;
    }

    public void setIsDirectoryGenerator(BooleanPercentageGenerator isDirectoryGenerator) {
        this.isDirectoryGenerator = isDirectoryGenerator;
    }
}
