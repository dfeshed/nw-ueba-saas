package fortscale.presidio.remote.conf;


import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.apache.commons.lang.StringUtils;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shays on 28/09/2017.
 */
public class ConfigrationServerClientUtilsMock implements ConfigrationServerClientUtils {


    public ConfigrationServerClientUtilsMock() {

    }

    /**
     * This method use to read single configuration file froms server and return the properties
     * @return Properties container
     * @throws Exception
     */
    public Properties readConfigurationAsProperties(String moduleName, String profile) throws Exception {
        Properties configProperties = new Properties();
        try {
            String fileName = moduleName;
            if (StringUtils.isNotBlank(profile)){
                fileName+="-"+profile;
            }

            fileName = "/config/"+fileName+".properties";
            InputStream inputStream = this.getClass().getResourceAsStream(fileName);
            configProperties.load(inputStream);

        }
        catch(Exception e){
            System.out.println("Could not load the file");
            e.printStackTrace();
        }

        return configProperties;

    }


}
