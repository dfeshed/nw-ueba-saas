package presidio.data.generators.fileentity;

import presidio.data.domain.FileSystemEntity;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;

public class FileSystemEntityGenerator implements IFileSystemEntityGenerator {

    IStringGenerator fileSystemTypeGenerator;
    IStringGenerator fileSystemLogonIDGenerator;

    public FileSystemEntityGenerator(String userName) throws GeneratorException {
        fileSystemTypeGenerator = new FileSystemTypeDefaultGenerator();
        fileSystemLogonIDGenerator = new FileSystemLogonIDGenerator(userName);
    }

    public FileSystemEntity getNext(){
        String fileSystemType = (String) getFileSystemTypeGenerator().getNext();
        String fileSystemLogonID = (String) getFileSystemLogonIDGenerator().getNext();

        return new FileSystemEntity(fileSystemType, fileSystemLogonID);
    }

    public IStringGenerator getFileSystemTypeGenerator() {
        return fileSystemTypeGenerator;
    }

    public void setFileSystemTypeGenerator(IStringGenerator fileSystemTypeGenerator) {
        this.fileSystemTypeGenerator = fileSystemTypeGenerator;
    }

    public IStringGenerator getFileSystemLogonIDGenerator() {
        return fileSystemLogonIDGenerator;
    }

    public void setFileSystemLogonIDGenerator(IStringGenerator fileSystemLogonIDGenerator) {
        this.fileSystemLogonIDGenerator = fileSystemLogonIDGenerator;
    }
}
