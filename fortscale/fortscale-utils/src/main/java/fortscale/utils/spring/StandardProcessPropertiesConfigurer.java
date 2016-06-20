package fortscale.utils.spring;

import fortscale.utils.logging.Logger;
import org.springframework.core.env.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Arrays;


/**
 * StandardProcessPropertiesConfigurer class is a configurer bean class that handles the process properties overrides.
 * <p>
 * Its ctor take a property file list and a Java Properties object. All of those override the previously defined properties.
 * <p>
 * A property order of importance is (from highest to lowest):
 * <p>
 * 1.   Overriding property object (to this class, if any)
 * 2.   Overriding property last file (of this class, if any)
 * ...
 * n-2. Overriding property 2nd file (to this class, if any)
 * n-1. Overriding property 1st file (to this class, if any)
 * n.   Standard property definition (defined else where)
 * <p>
 * The class extends PropertySourcesPlaceholderConfigurer
 * <p>
 * HOW TO USE:
 * <p>
 * The class should be used as a static bean.
 * <p>
 * EACH PROCESS MUST HAVE EXACTLY *ONE* INSTANCE OF THIS BEAN!
 * <p>
 * Example (in the process main configuration class):
 *
 * @Bean public static StandardProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {
 * <p>
 * String[] overridingFileList = {"weakest-overriding.properties", "medium-overriding.properties"};
 * <p>
 * Properties properties = new Properties();
 * properties.put("foo.goo,"strongest-override");
 * properties.put("foo.goo2,"strongest-override2");
 * <p>
 * StandardProcessPropertiesConfigurer configurer = new StandardProcessPropertiesConfigurer(overridingFileList, properties);
 * <p>
 * return configurer;
 * }
 * <p>
 * Created by gaashh &barak on 5/1/16.
 */
public class StandardProcessPropertiesConfigurer extends GenericPropertiesConfigurer {

    private static final Logger logger = Logger.getLogger(StandardProcessPropertiesConfigurer.class);
    private static final String overridingFileMsg =
            "##################### Fortscale %s configuration overrides ##################### \n# Modify an uncommented key=value tuple if you wish to edit default product configuration. i.e.: \n# fortscale.field.name.x=1 \n# Will update the value of fortscale.field.name.x to be 1 \n";

    private String groupName;
    private String processName;
    private String baseName = "common";
    private String baseOverridingProperties;
    private String groupOverridingProperties;
    private String processOverridingProperties;

    public StandardProcessPropertiesConfigurer() {
        super();
    }

    /**
     * list of standard overriding file list
     */
    @Override
    public void updateOverridingFileList() {
        processName = environment.getProperty("fortscale.process.name");
        groupName = environment.getProperty("fortscale.process.group.name");
        String baseConfigPath = environment.getProperty("fortscale.path.config");

        // common overriding properties path
        baseOverridingProperties = Paths.get(baseConfigPath, String.format("%s.properties", baseName)).toString();

        // group overriding properties path
        groupOverridingProperties = Paths.get(baseConfigPath, groupName, String.format("%s.properties", groupName)).toString();

        // process overriding properties path
        processOverridingProperties = Paths.get(baseConfigPath, groupName, String.format("%s.properties", processName)).toString();

        // create overriding files
        createStandardOverridingFiles();

        // the first overriding property is the strongest. the last is the weakest.
        this.overridePropertyFilesList = new String[]{processOverridingProperties, groupOverridingProperties, baseOverridingProperties};
        logger.debug("Updating overriding properties with files: {}", Arrays.toString(overridePropertyFilesList));
    }

    /**
     * Creates overriding files for base, group and process with commented example
     */
    private void createStandardOverridingFiles() {
        String baseOverridingFileMessage = String.format(overridingFileMsg, baseName);
        String groupOverridingFileMessage = String.format(overridingFileMsg, String.format("Group: %s", groupName));
        String processOverridingFileMessage = String.format(overridingFileMsg, String.format("Process: %s, Group:%s", processName, groupName));

        createOverridingFileIfNotExists(baseOverridingProperties, baseOverridingFileMessage);
        createOverridingFileIfNotExists(groupOverridingProperties, groupOverridingFileMessage);
        createOverridingFileIfNotExists(processOverridingProperties, processOverridingFileMessage);
    }

    /**
     * creates overriding file with written message
     *
     * @param filePath path of file to be created
     * @param message  message written to file
     */
    private void createOverridingFileIfNotExists(String filePath, String message) {
        File overridingFile = new File(filePath);
        try {
            if (!overridingFile.exists()) {
                File fileDir = overridingFile.toPath().getParent().toFile();
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                    logger.info("overriding properties dir: {} created", fileDir.getPath());
                }
                if (!overridingFile.exists()) {
                    overridingFile.createNewFile();
                    logger.info("overriding properties file: {} created", overridingFile.getPath());
                    FileWriter fileWriter = new FileWriter(overridingFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(message);
                    bufferedWriter.close();
                }
            }
        } catch (IOException e) {
            String msg = String.format("failed to create overriding file %s", filePath);
            logger.error(msg, e);
            throw new RuntimeException("Error creating overriding file", e);
        }
    }
}
