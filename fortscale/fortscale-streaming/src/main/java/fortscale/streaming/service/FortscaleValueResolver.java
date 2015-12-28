package fortscale.streaming.service;

import static fortscale.streaming.ConfigUtils.getConfigString;

import org.apache.samza.config.Config;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple EmbeddedValueResolverAware implementation.
 */
@Component
public class FortscaleValueResolver implements EmbeddedValueResolverAware {

    StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    public String resolveStringValue(String str) {
        return stringValueResolver.resolveStringValue(str);
    }

    public Boolean resolveBooleanValue(String str) {
        String value = stringValueResolver.resolveStringValue(str);
        return Boolean.parseBoolean(value);
    }

    public String resolveStringValue(Config config, String string) {
		return resolveStringValue(getConfigString(config, string));
	}

    public Boolean resolveBooleanValue(Config config, String string) {
        String value = resolveStringValue(config,string);
        return Boolean.parseBoolean(value);
    }

    public List<String> resolveStringValues(List<String> list) {
        List<String> resolvedList = new ArrayList<String>(list.size());

        for (String str : list)
            resolvedList.add(resolveStringValue(str));

        return resolvedList;
    }
}
