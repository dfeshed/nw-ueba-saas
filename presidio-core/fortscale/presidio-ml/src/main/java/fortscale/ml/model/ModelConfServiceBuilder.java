package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 9/11/17.
 */
public class ModelConfServiceBuilder {
    private final Map<String, AslConfigurationPaths> groupNameToModelConfigurationPathsMap;
    private AslResourceFactory aslResourceFactory;

    public ModelConfServiceBuilder(Collection<AslConfigurationPaths> modelConfigurationPathsCollection, AslResourceFactory aslResourceFactory) {
        this.groupNameToModelConfigurationPathsMap = modelConfigurationPathsCollection.stream()
                .collect(Collectors.toMap(AslConfigurationPaths::getGroupName, Function.identity()));
        this.aslResourceFactory = aslResourceFactory;
    }

    public ModelConfService buildModelConfService(String groupName) {
        String[] groupNames = groupName.split("\\.");
        Assert.isTrue(groupNames.length <= 2, "a group name is expected to contain at most root group and sub group.");
        String rootGroupName = groupNames[0];
        String subGroupName = groupNames.length < 2 ? "" : groupNames[1];

        Assert.isTrue(groupNameToModelConfigurationPathsMap.containsKey(rootGroupName), String.format("Root group %s is not configured.", rootGroupName));

        AslConfigurationPaths modelConfigurationPaths = groupNameToModelConfigurationPathsMap.get(rootGroupName);
        ModelConfService modelConfService = new ModelConfService(
                getResources(modelConfigurationPaths.getBaseConfigurationPath(), subGroupName),
                getResources(modelConfigurationPaths.getOverridingConfigurationPath(), subGroupName),
                getResources(modelConfigurationPaths.getAdditionalConfigurationPath(), subGroupName));
        modelConfService.loadAslConfigurations();
        return modelConfService;
    }

    private Resource[] getResources(String rootGroupPath, String subGroupName){
        if(rootGroupPath == null){
            return null;
        }
        Resource[] resources = aslResourceFactory.getResources(rootGroupPath+ "*.json");
        //The following code is until we find away to configure the resolver to be case insensitive.
        if(StringUtils.isBlank(subGroupName)){
            return resources;
        } else {
            for(Resource resource: resources){
                if(resource.getFilename().equalsIgnoreCase(subGroupName+ ".json")){
                    return new Resource[]{resource};
                }
            }
            return null;
        }
    }
}
