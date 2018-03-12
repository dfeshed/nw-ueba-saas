package jsonValidation;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
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
@ContextConfiguration(classes = {ElasticMappingsTests.SpringConfig.class, ElasticsearchTestConfig.class})
public class ElasticMappingsTests {

    private final String PRESIDIO_OUTPUT_ALERT = "presidio-output-alert";
    private final String INIT_PATH = "fortscale/presidio-rpm/src/main/python/installation-scripts/version/1_0/migration/init_elasticsearch.py";
    private final String INIT_RUN_COMMAND = "fortscale/presidio-rpm/src/main/python/installation-scripts/version/1_0/migration/init_elasticsearch.py --resources_path /home/presidio/presidio-core/el-extensions --run_type test";


    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Value("${linux.mapping.path}")
    private String linuxMapping;

    @Value("${windows.mapping.path}")
    private String windosMapping;

    @Test
    public void IsMappingValidJsonFile() throws IOException {
        String path;
        if (System.getProperty("os.name").startsWith("Linux")) {
            path = linuxMapping;
        } else {
            path = windosMapping;
        }
        ObjectMapper mapper = new ObjectMapper();
        File folder = new File(path);
        List<File> list = getFilesFromFolder(folder);
        list.forEach(file -> {
            try {
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
                    JsonNode actualObj = mapper.readTree(everything);
                    Assert.assertNotNull(actualObj);
                } catch (Exception ex) {
                    Assert.fail();
                } finally {
                    br.close();
                }
            } catch (Exception ex) {
            }
        });
    }

    @Test
    public void runInitElastisearchScript() {
        if (System.getProperty("os.name").startsWith("Linux")) {
            File file = null;
            try {
                file = new File(INIT_PATH);
                file.setExecutable(true, false);
                Process p;
                p = Runtime.getRuntime().exec(INIT_RUN_COMMAND);
                p.waitFor();
                if (p.exitValue() != 0) {
                    Assert.fail("script fail");
                }
                elasticsearchOperations.indexExists(PRESIDIO_OUTPUT_ALERT);
                Assert.assertTrue("presidio-output-alert index created.", true);
            } catch (Exception e) {
                Assert.fail("test fail " + e.toString());
            } finally {
                if (file != null) {
                    file.setExecutable(false, false);
                }
            }
        }
    }


    @Configuration
    @EnableSpringConfigured
    public static class SpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer mappingsTestPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("linux.mapping.path", "fortscale/presidio-rpm/src/main/resources/installation-scripts/version/1_0/elasticsearch");
            properties.put("windows.mapping.path", "presidio-core\\fortscale\\presidio-rpm\\src\\main\\resources\\installation-scripts\\version\\1_0\\elasticsearch");
            properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
            properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
            properties.put("elasticsearch.host", "localhost");
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
