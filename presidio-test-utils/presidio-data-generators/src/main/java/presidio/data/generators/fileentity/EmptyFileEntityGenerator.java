package presidio.data.generators.fileentity;

import presidio.data.domain.FileEntity;

public class EmptyFileEntityGenerator implements IFileEntityGenerator {
    public FileEntity getNext() {return new FileEntity(null,null ,null,false,null);}
}
