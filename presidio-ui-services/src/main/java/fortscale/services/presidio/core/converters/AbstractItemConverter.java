package fortscale.services.presidio.core.converters;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shays on 09/09/2017.
 */
public abstract class AbstractItemConverter<T,C> {
    public static final String SEPARATOR_CHARS = ",";

//    public abstract List<T> convertFromUiStringForQueryObject(String commaSeperatedString);
//    public abstract C convertFromResponseForUi(T response);

    protected Set<String> splitAndTrim(String values, boolean toLowerCase){
        if (StringUtils.isEmpty(values)){
            return Collections.emptySet();
        }
        Set<String> valuesSet = new HashSet<>();
        String[] array = StringUtils.split(values, SEPARATOR_CHARS);
        for (String value:array){
            value = StringUtils.trimToNull(value);
            if (value!=null){
                if (toLowerCase){
                    value = StringUtils.lowerCase(value);
                }
                valuesSet.add(value);
            }
        }

        return valuesSet;
    }


}
