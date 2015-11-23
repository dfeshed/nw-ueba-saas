package fortscale.collection;

import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Created by rans on 29/10/15.
 */
public class FsParametrizedMultiLineTest {
    @Rule
    public TestName testName = new TestName();


    protected String testCase;
    protected Object[] lines;
    protected Object[] outputs;

    public FsParametrizedMultiLineTest(String testCase, Object[] lines, Object[] outputs){
        this.testCase = testCase;
        this.lines = lines;
        this.outputs = outputs;
    }
}
