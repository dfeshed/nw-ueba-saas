package fortscale.services;

import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ThemesServiceImpl implements ThemesService{



    @Value("${presidio.themes.module.name}")
    private String module;
    @Value("${presidio.themes.default.profile.name}")
    private String profile;

    private ConfigrationServerClientUtils configrationServerClientUtils = new ConfigrationServerClientUtils();

    public Map<String,String> getDefaultTheme(){
        try {
            Properties p = configrationServerClientUtils.readConfigurationAsProperties(module,profile);
            Map<String,String> cssVariables = new HashMap<>();
            p.stringPropertyNames().forEach(varKey -> {
                    String valueAsString = p.getOrDefault(varKey,"").toString();
                    cssVariables.put(varKey,valueAsString);

            });
            return cssVariables;
        } catch (Exception e) {
            throw new RuntimeException("Cannot load themes",e);
        }

    }

}
