package fortscale.streaming.service;

import com.google.common.collect.Sets;
import org.apache.samza.config.Config;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static fortscale.streaming.ConfigUtils.getConfigString;

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

    /**
     *
     * @param key
     * @param delimiter placeholders resolved values will be splited into array by this delimiter
     * @return example:
     * for given properties:
     * a=1
     * b=2
     * c=${a},${b}
     * and params: key="c",delimiter=","
     * result will be: [1,2]
     *
     */
    public String[] resolveStringValueToStringArray(String key, String delimiter) {
        return resolveStringValue(key).split(delimiter);
    }

    /**
     *@see this#resolveStringValueToStringArray(String, String)
     */
    public Set<String> resolveStringValueToSet(String key, String delimiter)
    {
        return Sets.newHashSet(resolveStringValueToStringArray(key, delimiter));
    }
}
