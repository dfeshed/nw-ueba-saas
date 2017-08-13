package fortscale.configuration.sourceloader;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Strategy to load '.json' files into a {@link PropertySource}.
 */
public class JsonPropertySourceLoader implements PropertySourceLoader {

    public String[] getFileExtensions() {
        //  Configuration file format （ Extension name ）
        return new String[] { "json" };
    }

    public PropertySource<?> load(String name, Resource resource, String profile) throws IOException {
        if (profile == null) {
            Map<String, Object> result = mapPropertySource(resource);
            return new MapPropertySource(name, result);
        }
        return null;
    }

    /**
     *  Analysis Resource by Map
     *
     * @param resource
     * @return
     * @throws IOException
     */
    private Map<String, Object> mapPropertySource(Resource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = parser.parseMap(readFile(resource));
        flatMap("", result, map);
        return result;
    }


    /**
     *  Read Resource
     *
     * @param resource
     * @return The file content in string
     * @throws IOException
     */
    private String readFile(Resource resource) throws IOException {
        InputStream inputStream = resource.getInputStream();
        String content = FileCopyUtils.copyToString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return content;
    }

    /**
     * Handle map（map may be nested in map，recursive processing)，
     * the final output is a non nested map
     *
     * @param prefix - prefix
     * @param result - Treated map
     * @param map - Pre processed map
     */
    private void flatMap(String prefix, Map<String, Object> result, Map<String, Object> map) {
        if (prefix.length() > 0) {
            prefix += ".";
        }
        for (Map.Entry<String, Object> entrySet : map.entrySet()) {
            if (entrySet.getValue() instanceof Map) {
                flatMap(prefix + entrySet.getKey(), result, (Map<String, Object>) entrySet.getValue());
            } else {
                result.put(prefix + entrySet.getKey().toString(), entrySet.getValue());
            }
        }
    }
}
