package fortscale.collection;

/**
 * Created by rans on 29/10/15.
 */
public class FsParametrizedTest {


    protected String testCase;
    protected String line;
    protected String output;
    public FsParametrizedTest(String testCase, String line, String output){
        this.testCase = testCase;
        this.line = line;
        this.output = output;
    }
}
