package presidio.data.generators.fileentity;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IBooleanGenerator;
import presidio.data.generators.common.ILongGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.domain.FileEntity;

/**
 * Default generator for File entity.
 * - creates both file and folder entities in 9:1 ratio
 * - folder size is x10 of file size generator values
 * - 20% of entities on shared drive
 * - other values - defined in default generators of the fields
 *
 * **/
public class FileEntityGenerator implements IFileEntityGenerator {

    IStringGenerator fileNameGenerator;
    IStringGenerator filePathGenerator;
    ILongGenerator fileSizeGenerator;
    IBooleanGenerator isDriveSharedGenerator;
    IBooleanGenerator isDirectoryGenerator;

    public FileEntityGenerator() throws GeneratorException {
        fileNameGenerator = new FileNameDefaultExtGenerator();
        filePathGenerator = new SimplePathGenerator();
        fileSizeGenerator = new FileSizeIncrementalGenerator();

        isDriveSharedGenerator = new BooleanPercentageGenerator(20); // 20% shared
        isDirectoryGenerator = new BooleanPercentageGenerator(10);    // 10% directories
    }

    public FileEntity getNext(){
        Boolean isDirectory = (Boolean) getIsDirectoryGenerator().getNext();
        String fileName = (String) getFileNameGenerator().getNext();
        String filePath = (String) getFilePathGenerator().getNext();
        Long fileSize = getFileSizeGenerator().getNext() * (isDirectory ? 10:1); // make directory size larger
        Boolean isDriveShared = (Boolean) getIsDriveSharedGenerator().getNext();

        return new FileEntity(fileName, filePath ,fileSize, isDriveShared, isDirectory);
    }

    public IBooleanGenerator getIsDriveSharedGenerator() {
        return isDriveSharedGenerator;
    }

    public void setIsDriveSharedGenerator(IBooleanGenerator isDriveSharedGenerator) {
        this.isDriveSharedGenerator = isDriveSharedGenerator;
    }

    public IStringGenerator getFileNameGenerator() {
        return fileNameGenerator;
    }

    public void setFileNameGenerator(IStringGenerator fileNameGenerator) {
        this.fileNameGenerator = fileNameGenerator;
    }

    public IStringGenerator getFilePathGenerator() {
        return filePathGenerator;
    }

    public void setFilePathGenerator(IStringGenerator filePathGenerator) {
        this.filePathGenerator = filePathGenerator;
    }

    public ILongGenerator getFileSizeGenerator() {
        return fileSizeGenerator;
    }

    public void setFileSizeGenerator(ILongGenerator fileSizeGenerator) {
        this.fileSizeGenerator = fileSizeGenerator;
    }

    public IBooleanGenerator getIsDirectoryGenerator() {
        return isDirectoryGenerator;
    }

    public void setIsDirectoryGenerator(IBooleanGenerator isDirectoryGenerator) {
        this.isDirectoryGenerator = isDirectoryGenerator;
    }
}
