package presidio.data.generators.common;

import com.mifmif.common.regex.Generex;

/**
 * generates all possible values for given regex and stores them in a cyclic array.
 *
 * -can be used to generate strings by patterns i.e. contextId
 *
 * i.e. for pattern: "userId\\#[a-f]{1}[1-3]{1}"
 * result would be:
 * userId#a1, userId#a2, userId#a3,
 * userId#b1, userId#b2, userId#b3,
 * userId#c1, userId#c2, userId#c3,
 * userId#d1, userId#d2, userId#d3,
 * userId#e1, userId#e2, userId#e3,
 * userId#f1, userId#f2, userId#f3
 *
 * @see <a href="https://github.com/mifmif/Generex" >how the magix is done</a>
 *
 * Created by barak_schuster on 9/4/17.
 */
public class StringRegexCyclicValuesGenerator  extends CyclicValuesGenerator<String> implements IStringGenerator {

    public StringRegexCyclicValuesGenerator(String pattern) {
        super(new Generex(pattern).getAllMatchedStrings().toArray(new String[0]));
    }
}
