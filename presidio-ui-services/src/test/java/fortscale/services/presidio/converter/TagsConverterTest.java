package fortscale.services.presidio.converter;

import fortscale.services.presidio.core.converters.TagsConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Created by shays on 11/09/2017.
 */
public class TagsConverterTest {

    TagsConverter tagsConverter = new TagsConverter();

    @Test
    public void testTagsFromUiFilterToQuery(){
        List<String> s=tagsConverter.convertUiFilterToQueryDto("admin",null);
        Assert.assertEquals(1,s.size());

        List<String> s2=tagsConverter.convertUiFilterToQueryDto("noadmin",null);
        Assert.assertEquals(0,s2.size());

        List<String> s3=tagsConverter.convertUiFilterToQueryDto((String)null,null);
        Assert.assertEquals(0,s3.size());

        List<String> s4=tagsConverter.convertUiFilterToQueryDto("any",null);
        Assert.assertEquals(1,s4.size());
        Assert.assertEquals("admin",s4.get(0));

    }

}
