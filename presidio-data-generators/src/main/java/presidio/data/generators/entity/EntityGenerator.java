package presidio.data.generators.entity;

import presidio.data.generators.common.StringCyclicValuesGenerator;

public class EntityGenerator implements IEntityGenerator {

    private StringCyclicValuesGenerator iterator = new StringCyclicValuesGenerator(new String[] {"ja3", "sslSubject"});
    private Imd5Generator ja3Generator;
    private ISslSubjectGenerator sslSubjectGenerator;
    private String ja3;
    private String sslSubject;

    public EntityGenerator(IBaseGenerator ja3Generator, IBaseGenerator sslSubjectGenerator) {
        if ( (ja3Generator instanceof Imd5Generator) && (sslSubjectGenerator instanceof ISslSubjectGenerator)) {
            this.ja3Generator = (Imd5Generator) ja3Generator;
            this.sslSubjectGenerator = (ISslSubjectGenerator) sslSubjectGenerator;
            ja3 = this.ja3Generator.getNext();
            sslSubject = this.sslSubjectGenerator.getNext();
        } else throw new RuntimeException("Type mismatch");
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
