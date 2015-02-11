package fortscale.streaming.service;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple EmbeddedValueResolverAware implementation.
 */
@Component
public class FortscaleStringValueResolver implements EmbeddedValueResolverAware {

    StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    public String resolveStringValue(String str) {
        return stringValueResolver.resolveStringValue(str);
    }

    public List<String> resolveStringValues(List<String> list) {
        List<String> resolvedList = new ArrayList<String>(list.size());

        for (String str : list)
            resolvedList.add(resolveStringValue(str));

        return resolvedList;
    }
}
