package fortscale.utils.elasticsearch.services;


import fortscale.utils.elasticsearch.annotations.Template;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class TemplateAnnotationExtractor implements TemplateExtractor {
    private static final Logger logger = LoggerFactory.getLogger(TemplateAnnotationExtractor.class);


    public TemplateAnnotationExtractor() {
    }

    @Override
    public String mappingConverting(Class<?> clazz) {
        String path;
        if (clazz.isAnnotationPresent(Mapping.class)) {
            path = clazz.getAnnotation(Mapping.class).mappingPath();
        } else {
            if (clazz.isAnnotationPresent(Template.class)) {
                path = clazz.getAnnotation(Template.class).mappingPath();
            } else {
                logger.debug("Missing annotation.");
                return null;
            }
        }
        if (isNotBlank(path)) {
            if (clazz.isAnnotationPresent(Mapping.class)) {
                return readFileFromClasspath(path);
            } else {
                return getMappingFromTemplate(path);
            }
        } else {
            logger.debug("Mapping is empty in path: {}.", path);
        }
        return null;
    }

    private String getMappingFromTemplate(String templatePath) {
        try {
            String template = readFileFromClasspath(templatePath);
            JSONObject jsonObj = new JSONObject(template);
            Iterator itr = jsonObj.keys();
            while (itr.hasNext()) {
                String key = itr.next().toString();
                if (key.toString().contains("template")) {
                    JSONObject templateJson = (JSONObject) jsonObj.get(key.toString());
                    Object mappings = templateJson.get("mappings");
                    return mappings.toString();
                }
            }
        } catch (JSONException ex) {
            logger.debug("Mapping is im template: {}.", templatePath);
            return null;
        }
        return null;
    }

    @Override
    public String settingsConverting(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Setting.class)) {
            String settingPath = clazz.getAnnotation(Setting.class).settingPath();
            if (isNotBlank(settingPath)) {
                return readFileFromClasspath(settingPath);
            }
        }
        return null;
    }

    public static String readFileFromClasspath(String url) {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = null;

        try {
            ClassPathResource classPathResource = new ClassPathResource(url);
            InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            String lineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(lineSeparator);
            }
        } catch (Exception e) {
            logger.debug(String.format("Failed to load file from url: %s: %s", url, e.getMessage()));
            return null;
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.debug(String.format("Unable to close buffered reader.. %s", e.getMessage()));
                }
        }

        return stringBuilder.toString();
    }
}
