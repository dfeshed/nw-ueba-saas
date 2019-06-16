package presidio.data.generators.entity;

import presidio.data.domain.event.Entity;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.common.md5.Imd5Generator;
import presidio.data.generators.ssl_subject.ISslSubjectGenerator;

public class EntityGenerator implements IEntityGenerator {

    private StringCyclicValuesGenerator iterator = new StringCyclicValuesGenerator(new String[] {"ja3", "sslSubject"});
    private IBaseGenerator<String> ja3Generator;
    private IBaseGenerator<String> sslSubjectGenerator;
    private String ja3;
    private String sslSubject;

    public EntityGenerator(IBaseGenerator<String> ja3Generator, IBaseGenerator<String> sslSubjectGenerator) {
        this.ja3Generator = ja3Generator;
        this.sslSubjectGenerator = sslSubjectGenerator;
        ja3 = ja3Generator.getNext();
        sslSubject = sslSubjectGenerator.getNext();
    }


    @Override
    public Entity getNext() {

        Entity entity = new Entity(ja3, sslSubject);

        switch (iterator.getNext()) {
            case "ja3":         ja3 = ja3Generator.getNext(); break;
            case "sslSubject":  sslSubject = sslSubjectGenerator.getNext(); break;
            default: throw new RuntimeException("no such key");
        }

        return entity;
    }
}
