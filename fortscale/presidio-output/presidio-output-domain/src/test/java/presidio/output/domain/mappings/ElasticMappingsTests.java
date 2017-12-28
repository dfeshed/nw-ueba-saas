package presidio.output.domain.mappings;


import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
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
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i].getAbsoluteFile()));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                JSONObject jsonObject = new JSONObject(everything);
                Assert.assertNotNull(jsonObject);
            } catch (Exception ex) {
                Assert.fail();
            } finally {
                br.close();
            }
        }
    }

    @Configuration
    @EnableSpringConfigured
    public static class SpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer mappingsTestPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("linux.mapping.path", "presidio/presidio-core/el-extensions/mappings/");
            properties.put("windos.mapping.path", "src\\main\\resources\\elasticsearch\\mappings");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

}
