package jsonValidation;


import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ElasticMappingsTests.SpringConfig.class)
public class ElasticMappingsTests {

    @Value("${linux.mapping.path}")
    private String linuxMapping;

    @Value("${windos.mapping.path}")
    private String windosMapping;

    @Test
    public void IsMappingValidJsonFile() throws IOException {
        String path;
        if (System.getProperty("os.name").startsWith("Linux")) {
            path = linuxMapping;
        } else {
            path = windosMapping;
        }
        File folder = new File(path);
        List<File> list = getFilesFromFolder(folder);
        list.forEach(file -> {
            try {
                System.out.println(file.toString());
                BufferedReader br = new BufferedReader(new FileReader(((File) file).getAbsoluteFile()));
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    String everything = sb.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(everything);
                        Assert.assertNotNull(jsonObject);
                    } catch (JSONException ex) {
                        JSONArray jsonArray = new JSONArray(everything);
                        Assert.assertNotNull(jsonArray);
                    }
                } catch (Exception ex) {
                    Assert.fail();
                } finally {
                    br.close();
                }
            } catch (Exception ex) {
            }
        });
    }

    @Configuration
    @EnableSpringConfigured
    public static class SpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer mappingsTestPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("linux.mapping.path", "presidio/presidio-core/el-extensions/");
            properties.put("windos.mapping.path", "src\\main\\resources\\elasticsearch");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

    private List<File> getFilesFromFolder(File file) {
        List<File> fileList = new LinkedList<>();
        if (file.isDirectory()) {
            File[] listOfFolders = file.listFiles();
            for (int i = 0; i < listOfFolders.length; i++) {
                fileList.addAll(getFilesFromFolder(listOfFolders[i]));
            }
        } else {
            fileList.add(file);
        }
        return fileList;
    }

}
