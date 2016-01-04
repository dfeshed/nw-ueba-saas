package fortscale.collection.jobs.gds.populators.enrichment;

import fortscale.collection.jobs.gds.GDSInputHandler;
import fortscale.collection.jobs.gds.GDSStandardInputHandler;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSIPResolvingCLIPopulator implements GDSConfigurationPopulator {

    private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        //source Ip Resolving task
        if (paramsMap.containsKey("sourceIpResolvingFlag") && paramsMap.get("sourceIpResolvingFlag").getParamFlag()) {

            System.out.println(String.format("Going to configure the IP resolving task for %s", dataSourceName));

            System.out.println(String.format("Does %s resolving is restricted to AD name (in case of true and the machine doesnt exist in the AD it will not return it as resolved value) (y/n) ?", dataSourceName));

            paramsMap.put("restrictToAD", new ConfigurationParam("restrictToAD", GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), ""));

            System.out.println(String.format("Does %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?", dataSourceName));

            paramsMap.put("shortNameUsage", new ConfigurationParam("shortNameUsage", GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), ""));

            System.out.println(String.format("Does %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?", dataSourceName));

            paramsMap.put("removeLastDotUsage", new ConfigurationParam("removeLastDotUsage", GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), ""));

            System.out.println(String.format("Does %s resolving need to drop in case of resolving fail (y/n) ?", dataSourceName));
            paramsMap.put("dropOnFailUsage", new ConfigurationParam("dropOnFailUsage", GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), ""));

            System.out.println(String.format("Does %s resolving need to override the source ip field with the resolving value (y/n) ?", dataSourceName));

            paramsMap.put("overrideIpWithHostNameUsage", new ConfigurationParam("overrideIpWithHostNameUsage", GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), ""));

            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "IpResolvingStreamTask_sourceIp"));
            if (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag())
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-source-ip-resolved"));

            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-resolved"));

            paramsMap.put("ipField", new ConfigurationParam("ipField", false, String.format("${impala.data.%s.table.field.source}", dataSourceName)));
            paramsMap.put("host", new ConfigurationParam("host", false, String.format("${impala.data.%s.table.field.source_name}", dataSourceName)));

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "IpResolvingStreamTask"));
        }


        //target ip resolving
        if (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag()) {

            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "IpResolvingStreamTask_targetIp"));
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-resolved"));
            paramsMap.put("ipField", new ConfigurationParam("ipField", false, String.format("${impala.data.%s.table.field.target}", dataSourceName)));
            paramsMap.put("host", new ConfigurationParam("host", false, String.format("${impala.data.%s.table.field.target_name}", dataSourceName)));

            System.out.println("Finished to configure IP resolving streaming task for target. Do you want to apply changes now? (y/n)");

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "IpResolvingStreamTask"));
        }

        return paramsMap;
    }
}
