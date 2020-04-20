package presidio.data.generators.fileentity;

import presidio.data.domain.FileEntity;
import presidio.data.generators.fileentity.IFileEntityGenerator;

public class NullFileEntityGenerator implements IFileEntityGenerator {
    public FileEntity getNext() {return new FileEntity(null,null ,null,null,null);}
}
