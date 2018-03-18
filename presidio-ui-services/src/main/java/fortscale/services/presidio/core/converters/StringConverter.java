package fortscale.services.presidio.core.converters;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by shays on 09/09/2017.
 */
public class StringConverter extends AbstractItemConverter{

    public List convertUiFilterToQueryDto(String commaSeperatedString) {

        if (StringUtils.isEmpty(commaSeperatedString)){
            return Collections.EMPTY_LIST;
        }

        //Support only admin tags
        Set<String> stringResults = splitAndTrim(commaSeperatedString,false);
        return new ArrayList(stringResults);
    }



}
