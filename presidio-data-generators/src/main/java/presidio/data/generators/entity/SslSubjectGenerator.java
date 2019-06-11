package presidio.data.generators.entity;

public class SslSubjectGenerator extends DictionaryCyclicGenerator implements ISslSubjectGenerator {

    public SslSubjectGenerator(int fromIndex, int numOfEntities) {
        super(fromIndex, numOfEntities,SslSubjects.COMPANIES_500);
    }

    @Override
    public String getNext() {
       return super.getNext();
    }

}
