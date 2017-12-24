package presidio.output.domain.mappings;


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@RunWith(SpringRunner.class)
public class ElasticMappingsTests {

    private final String PATH = "/home/presidio/presidio-core/el-extensions/mappings/";


    @Test
    public void IsMappingValidJsonFile() throws IOException {

        File folder = new File(PATH);
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

}
