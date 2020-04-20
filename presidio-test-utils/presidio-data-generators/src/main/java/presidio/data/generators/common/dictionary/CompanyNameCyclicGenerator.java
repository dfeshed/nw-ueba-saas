package presidio.data.generators.common.dictionary;

public class CompanyNameCyclicGenerator extends DictionaryCyclicGenerator {

    public CompanyNameCyclicGenerator(int fromIndex) {
        super(fromIndex, CompanyNames.COMPANIES_500.length-1, CompanyNames.COMPANIES_500);
    }

    public CompanyNameCyclicGenerator(int fromIndex, int toIndex) {
        super(fromIndex, toIndex, CompanyNames.COMPANIES_500);
    }
}
