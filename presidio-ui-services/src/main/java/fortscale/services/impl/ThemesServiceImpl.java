package fortscale.services.impl;

import fortscale.services.ThemesService;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class ThemesServiceImpl implements ThemesService {

    private String module;
    private String profile;

    private String CSS_VAR_PREFIX="css.var.";

    private ConfigrationServerClientUtils configrationServerClientUtils = new ConfigrationServerClientUtils();

    @PostConstruct
    public void init(){
        module = SpringPropertiesUtil.getProperty("presidio.themes.module.name");
        profile =SpringPropertiesUtil.getProperty("presidio.themes.default.profile.namee");
    }
    public Map<String,String> getDefaultTheme(){
        try {
            Properties p = configrationServerClientUtils.readConfigurationAsProperties(module,profile);
            Map<String,String> cssVariables = new HashMap<>();
            p.stringPropertyNames().forEach(varKey -> {
                if (varKey.startsWith(CSS_VAR_PREFIX)) {
                    String valueAsString = p.getOrDefault(varKey, "").toString();
                    String normalizedVarKey = varKey.replace(CSS_VAR_PREFIX,"");
                    cssVariables.put(normalizedVarKey, valueAsString);
                }

            });
            return cssVariables;
        } catch (Exception e) {
            throw new RuntimeException("Cannot load themes",e);
        }

    }

}
