package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.state.EnrichmentDefinitionState;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.*;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSEnrichmentConfigurator extends GDSBaseConfigurator{

    private static Logger logger = Logger.getLogger(GDSEnrichmentConfigurator.class);

    private ConfigurationService userNormalizationTaskService;
    private ConfigurationService ipResolvingTaskService;
    private ConfigurationService computerTaggingTaskService;
    private ConfigurationService geoLocationTaskService;
    private ConfigurationService userMongoUpdateTaskService;
    private ConfigurationService hdfsTaskService;

    public GDSEnrichmentConfigurator() {
        ipResolvingTaskService = new IpResolvingTaskConfiguration();
        userNormalizationTaskService = new UserNormalizationTaskConfiguration();
        computerTaggingTaskService = new ComputerTaggingClassConfiguration();
        geoLocationTaskService = new GeoLocationConfiguration();
        userMongoUpdateTaskService = new UserMongoUpdateConfiguration();
        hdfsTaskService = new HDFSWriteTaskConfiguration();
    }

    /**
     * This method will configure the entire streaming configuration - Enrich , Single model/score, Aggregation
     */
    public void configure() throws Exception {

        super.configure();

        EnrichmentDefinitionState enrichmentDefinitionState = gdsConfigurationState.getEnrichmentDefinitionState();

        Map<String, ConfigurationParam> paramsMap = enrichmentDefinitionState.getParamsMap();

        paramsMap.put("dataSourceName", new ConfigurationParam("dataSourceName", false, gdsConfigurationState.getDataSourceName()));
        paramsMap.put("dataSourceType", new ConfigurationParam("dataSourceType", false, gdsConfigurationState.getEntityType().name().toLowerCase()));
        paramsMap.put("dataSourceLists", new ConfigurationParam("dataSourceLists", false, gdsConfigurationState.getCurrentDataSources()));

        String dataSourceName = gdsConfigurationState.getDataSourceName();

        System.out.println(String.format("Does %s need to pass through enrich steps at the Streaming (y/n) ?", dataSourceName));

        if(!GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            return;
        }

        // configure new Topic to the data source or use the GDS general topic
        System.out.println(String.format("Does %s use the general GDS streaming topology (y/n) ?", dataSourceName));

        paramsMap.put("topologyFlag",new ConfigurationParam("topologyFlag",true,""));
        paramsMap.put("lastState", new ConfigurationParam("lastState",false,"etl"));
        paramsMap.put("taskName",new ConfigurationParam("taskName",false,"UsernameNormalizationAndTaggingTask"));

        System.out.println(String.format("Does %s have target username to normalized (y/n) ?",dataSourceName));

        boolean targetNormalizationFlag = false;
        //in case there is a target user to be normalize also
        if(GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            targetNormalizationFlag = true;
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_normalized_target_user"));
        }
        else if ((paramsMap.containsKey("sourceIpResolvingFlag") && paramsMap.get("sourceIpResolvingFlag").getParamFlag()) || (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag())) {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
        }
        //in case there is machine to normalized and tag
        else if ((paramsMap.containsKey("sourceMachineNormalizationFlag") && paramsMap.get("sourceMachineNormalizationFlag").getParamFlag()) || (paramsMap.containsKey("targetMachineNormalizationFlag") && paramsMap.get("targetMachineNormalizationFlag").getParamFlag())) {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
        }
        //in case there is ip to geo locate
        else if ((paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) || (paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag())) {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
        }
        else {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event"));
        }

        //User name field
        paramsMap.put("userNameField", new ConfigurationParam("userNameField",false,"username"));

        //Domain field  - for the enrich part
        System.out.println(String.format("Does %s have a field that contain the user domain  (y/n) ?",dataSourceName));

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()))
        {
            //Domain field  - for the enrich part
            System.out.println(String.format("pleaase enter the field name that will contain the user Domain value :"));

            paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, gdsInputHandler.getInput()));
        }
        else {
            paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, "fake"));

            //In case of fake domain - enter the actual domain value the PS want
            paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, ""));
        }

        //Normalized_username field
        paramsMap.put("normalizedUserNameField", new ConfigurationParam("normalizedUserNameField",false,"${impala.table.fields.normalized.username}"));

        //TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
        //Normalizing service
        System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appeare there (i.e what we do for kerberos) (y/n):",dataSourceName));

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            //Service  name
            paramsMap.put("normalizeSservieName", new ConfigurationParam("normalizeSservieName",false,"SecurityUsernameNormalizationService"));
            paramsMap.put("updateOnlyFlag", new ConfigurationParam("updateOnlyFlag",true,"true"));

        } else {
            paramsMap.put("normalizeSservieName", new ConfigurationParam("normalizeSservieName",false,"genericUsernameNormalizationService"));
            paramsMap.put("updateOnlyFlag", new ConfigurationParam("updateOnlyFlag",false,"false"));
        }

        userNormalizationTaskService.setConfigurationParams(paramsMap);

        System.out.println("Finished to configure user normalization streaming task. Do you want to apply changes now? (y/n)");

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            if (userNormalizationTaskService.init()) {
                userNormalizationTaskService.applyConfiguration();
            }
        }

        System.out.println("Do you want to reset user normalization streaming task changes? (y/n)");

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            reset();
        }


        if (targetNormalizationFlag) {
            if ((paramsMap.containsKey("sourceIpResolvingFlag") && paramsMap.get("sourceIpResolvingFlag").getParamFlag()) || (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
                //in case there is machine to normalized and tag
            else if ((paramsMap.containsKey("sourceMachineNormalizationFlag") && paramsMap.get("sourceMachineNormalizationFlag").getParamFlag()) || (paramsMap.containsKey("targetMachineNormalizationFlag") && paramsMap.get("targetMachineNormalizationFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
                //in case there is ip to geo locate
            else if ((paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) || (paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event"));

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "UsernameNormalizationAndTaggingTask"));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "UsernameNormalizationAndTaggingTask_target"));

            paramsMap.put("userNameField", new ConfigurationParam("userNameField", false, gdsInputHandler.getInput()));

            //Domain field  - for the enrich part
            System.out.println(String.format("Does %s have a field that contain the target user domain  (y/n) ?", dataSourceName));

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                //Domain field  - for the enrich part
                System.out.println("Please enter the field name that will contain the target user Domain value :");

                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, gdsInputHandler.getInput()));

                paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, ""));

            } else {
                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, "fake"));
                paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, dataSourceName + "Connect"));


            }
            System.out.println("Please enter the field name of the field that will contain the second normalized user name :");

            paramsMap.put("normalizedUserNameField", new ConfigurationParam("normalizedUserNameField", false, gdsInputHandler.getInput()));

            userNormalizationTaskService.setConfigurationParams(paramsMap);

            System.out.println("Finished to configure user normalization streaming task for target user. Do you want to apply changes now? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                if (userNormalizationTaskService.init()) {
                    userNormalizationTaskService.applyConfiguration();
                }
            }
        }

        userNormalizationTaskService.done();



        System.out.println(String.format("End configure the Normalized Username and tagging task for %s", dataSourceName));
        paramsMap.put("lastState", new ConfigurationParam("lastState", false, "UsernameNormalizationAndTaggingTask"));



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

            paramsMap.put("ipField", new ConfigurationParam("ipField", false, String.format("${impala.data.%s.table.field.source}",dataSourceName)));
            paramsMap.put("host", new ConfigurationParam("host", false, String.format("${impala.data.%s.table.field.source_name}",dataSourceName)));

            ipResolvingTaskService.setConfigurationParams(paramsMap);

            System.out.println("Finished to configure IP resolving streaming task for source. Do you want to apply changes now? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                if (ipResolvingTaskService.init()) {
                    ipResolvingTaskService.applyConfiguration();
                }
            }

            System.out.println("Do you want to reset ip resolving streaming task changes? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                reset();
            }

            paramsMap.put("lastState", new ConfigurationParam("lastState",false,"IpResolvingStreamTask"));
        }

        //target ip resolving
        if (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag()) {

            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "IpResolvingStreamTask_targetIp"));
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-resolved"));
            paramsMap.put("ipField", new ConfigurationParam("ipField", false,  String.format("${impala.data.%s.table.field.target}",dataSourceName)));
            paramsMap.put("host", new ConfigurationParam("host", false,  String.format("${impala.data.%s.table.field.target_name}",dataSourceName)));

            System.out.println("Finished to configure IP resolving streaming task for target. Do you want to apply changes now? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                if (ipResolvingTaskService.init()) {
                    ipResolvingTaskService.applyConfiguration();
                }
            }

            System.out.println("Do you want to reset ip resolving streaming task changes? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                reset();
            }

            paramsMap.put("lastState", new ConfigurationParam("lastState",false,"IpResolvingStreamTask"));
        }

        ipResolvingTaskService.done();


        //Computer tagging task
        if (((paramsMap.containsKey("sourceMachineNormalizationFlag") && paramsMap.get("sourceMachineNormalizationFlag").getParamFlag()) || (paramsMap.containsKey("targetMachineNormalizationFlag") && paramsMap.get("targetMachineNormalizationFlag").getParamFlag())))
        {
            System.out.println(String.format("Going to configure the Computer tagging and normalization task for %s", dataSourceName));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "ComputerTaggingClusteringTask"));

            if((paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) || (paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-computer-tagged-clustered_to_geo_location"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-computer-tagged-clustered"));

            // configure new configuration for the new dta source for source_ip
            System.out.println(String.format("Does %s source machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
            Boolean ensureComputerExist = gdsInputHandler.getInput().equals("y") || gdsInputHandler.getInput().equals("yes");
            paramsMap.put("createNewComputerFlag",new ConfigurationParam("createNewComputerFlag",ensureComputerExist,""));
            paramsMap.put("srcMachineClassifier", new ConfigurationParam("srcMachineClassifier",false, String.format("${impala.data.%s.table.field.src_class}",dataSourceName)));
            paramsMap.put("srcHost", new ConfigurationParam("srcHost", false,  String.format("${impala.data.%s.table.field.source_name}",dataSourceName)));
            paramsMap.put("srcClusteringField", new ConfigurationParam("srcClusteringField",false, String.format("${impala.data.%s.table.field.normalized_src_machine}",dataSourceName)));
            paramsMap.put("dstMachineClassifier", new ConfigurationParam("dstMachineClassifier",false, String.format("${impala.data.%s.table.field.dst_class}",dataSourceName)));
            paramsMap.put("dstClusteringField", new ConfigurationParam("dstClusteringField",false, String.format("${impala.data.%s.table.field.normalized_dst_machine}",dataSourceName)));
            paramsMap.put("dstHost", new ConfigurationParam("dstHost", false,  String.format("${impala.data.%s.table.field.target_name}",dataSourceName)));

            computerTaggingTaskService.setConfigurationParams(paramsMap);
            System.out.println("Finished to configure computer tagging streaming task. Do you want to apply changes now? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                if (computerTaggingTaskService.init()) {
                    computerTaggingTaskService.applyConfiguration();
                }
            }

            System.out.println("Do you want to reset computer tagging streaming task changes? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                reset();
            }

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "ComputerTaggingClusteringTask"));
            System.out.println(String.format("End configure the Computer Tagging task for %s", dataSourceName));

            computerTaggingTaskService.done();
        }

        //Source Geo Location
        if(paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) {

            System.out.println(String.format("Going to configure the source ip at GeoLocation task for %s", dataSourceName));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "source_VpnEnrichTask"));

            if(paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag())
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-source-ip-geolocated"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-geolocated"));

            paramsMap.put("ipField", new ConfigurationParam("ipField",false,"${impala.data.%s.table.field.source_ip}"));
            paramsMap.put("countryField", new ConfigurationParam("ipField",false,"src_country"));
            paramsMap.put("longtitudeField", new ConfigurationParam("ipField",false,"src_longtitudeField"));
            paramsMap.put("latitudeField", new ConfigurationParam("ipField",false,"src_latitudeField"));
            paramsMap.put("countryIsoCodeField", new ConfigurationParam("ipField",false,"src_countryIsoCodeField"));
            paramsMap.put("regionField", new ConfigurationParam("ipField",false,"src_regionField"));
            paramsMap.put("cityField", new ConfigurationParam("ipField",false,"src_cityField"));
            paramsMap.put("ispField", new ConfigurationParam("ipField",false,"src_ispField"));
            paramsMap.put("usageTypeField", new ConfigurationParam("ipField",false,"src_usageTypeField"));
            paramsMap.put("DoesssionUpdateFlag", new ConfigurationParam("ipField",false,""));
            paramsMap.put("doDataBuckets", new ConfigurationParam("ipField",false,""));
            paramsMap.put("doGeoLocation", new ConfigurationParam("ipField",true,""));

            geoLocationTaskService.setConfigurationParams(paramsMap);

            System.out.println("Finished to configure source geo location streaming task. Do you want to apply changes now? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                if (geoLocationTaskService.init()) {
                    geoLocationTaskService.applyConfiguration();
                }
            }

            System.out.println("Do you want to reset source geo location streaming task changes? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                reset();
            }

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "VpnEnrichTask"));
        }

        //Target Geo Location
        if(paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag()) {

            System.out.println(String.format("Going to configure the target ip at  GeoLocation task for %s", dataSourceName));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "target_VpnEnrichTask"));


            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-geolocated"));

            paramsMap.put("ipField", new ConfigurationParam("ipField",false,"${impala.data.%s.table.field.target}"));
            paramsMap.put("countryField", new ConfigurationParam("countryField",false,"dst_country"));
            paramsMap.put("longtitudeField", new ConfigurationParam("longtitudeField",false,"dst_longtitudeField"));
            paramsMap.put("latitudeField", new ConfigurationParam("latitudeField",false,"dst_latitudeField"));
            paramsMap.put("countryIsoCodeField", new ConfigurationParam("countryIsoCodeField",false,"dst_countryIsoCodeField"));
            paramsMap.put("regionField", new ConfigurationParam("regionField",false,"dst_regionField"));
            paramsMap.put("cityField", new ConfigurationParam("cityField",false,"dst_cityField"));
            paramsMap.put("ispField", new ConfigurationParam("ispField",false,"dst_ispField"));
            paramsMap.put("usageTypeField", new ConfigurationParam("usageTypeField",false,"dst_usageTypeField"));
            paramsMap.put("DoesssionUpdateFlag", new ConfigurationParam("DoesssionUpdateFlag",false,""));
            paramsMap.put("doDataBuckets", new ConfigurationParam("doDataBuckets",false,""));
            paramsMap.put("doGeoLocation", new ConfigurationParam("doGeoLocation",true,""));

            geoLocationTaskService.setConfigurationParams(paramsMap);

            System.out.println("Finished to configure source geo location streaming task. Do you want to apply changes now? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                if (geoLocationTaskService.init()) {
                    geoLocationTaskService.applyConfiguration();
                }
            }

            System.out.println("Do you want to reset source geo location streaming task changes? (y/n)");

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                reset();
            }

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "VpnEnrichTask"));

            geoLocationTaskService.done();

        }
        System.out.println(String.format("End configure the GeoLocation task for %s", dataSourceName));


        //USER MONGO UPDATE
        System.out.println(String.format("Going to configure the UserMongoUpdate task for %s (i.e we use it for user last activity update) ", dataSourceName));

        paramsMap.put("taskName", new ConfigurationParam("taskName", false, String.format("UserMongoUpdateStreamTask",dataSourceName)));
        paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, ""));


        //Status field value
        System.out.println("Do you want to update last activity for any raw that came and not only successed events (y/n)? ");
        boolean isAnyRow = GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput());
        paramsMap.put("anyRow", new ConfigurationParam("anyRow", isAnyRow, ""));


        if (!isAnyRow) {
            //configure the field that represent the status
            //System.out.println(String.format("Please enter the field that will hold the message status   (i.e status,failure_code):"));
            //String statusFieldName =  gdsInputHandler.getInput();
            paramsMap.put("statusFieldName", new ConfigurationParam("statusFieldName", false, "status"));

            //SUCCESS  value
            System.out.println("Please enter value that mark event as successed (i.c Accepted for ssh or SUCCESS for vpn 0x0 for kerberos ) :");
            paramsMap.put("successValue", new ConfigurationParam("successValue", false, gdsInputHandler.getInput()));
        }


        userMongoUpdateTaskService.setConfigurationParams(paramsMap);
        System.out.println("Finished to configure user mongo update streaming task. Do you want to apply changes now? (y/n)");

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            if (userMongoUpdateTaskService.init()) {
                userMongoUpdateTaskService.applyConfiguration();
            }
        }

        System.out.println("Do you want to reset user mongo update streaming task changes? (y/n)");

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            reset();
        }

        userMongoUpdateTaskService.done();
        System.out.println(String.format("End configure the UserMongoUpdate task for %s", dataSourceName));


        //HDFS - WRITE
        System.out.println(String.format("Going to configure the HDFS write task for the enrich for %s  ", dataSourceName));

        paramsMap.put("taskName", new ConfigurationParam("taskName", false, "enriched_HDFSWriterStreamTask"));
        paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-enriched-after-write"));
        paramsMap.put("fieldList", new ConfigurationParam("fieldList",false,String.format("${impala.enricheddata.%s.table.fields}",dataSourceName)));
        paramsMap.put("delimiter", new ConfigurationParam("delimiter",false,String.format("${impala.enricheddata.%s.table.delimiter}",dataSourceName)));
        paramsMap.put("tableName", new ConfigurationParam("tableName",false,String.format("${impala.enricheddata.%s.table.name}",dataSourceName)));
        paramsMap.put("hdfsPath", new ConfigurationParam("hdfsPath",false,String.format("${hdfs.user.enricheddata.%s.path}",dataSourceName)));
        paramsMap.put("fileName", new ConfigurationParam("fileName",false,String.format("${hdfs.enricheddata.%s.file.name}",dataSourceName)));
        paramsMap.put("partitionStrategy", new ConfigurationParam("partitionStrategy",false,String.format("${impala.enricheddata.%s.table.partition.type}",dataSourceName)));


        //todo -  add the anility to configure this param
        paramsMap.put("discriminatorsFields", new ConfigurationParam("discriminatorsFields",false,""));

        hdfsTaskService.setConfigurationParams(paramsMap);
        System.out.println("Finished to configure hdfs write streaming task. Do you want to apply changes now? (y/n)");

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            if (hdfsTaskService.init()) {
                hdfsTaskService.applyConfiguration();
            }
        }

        System.out.println("Do you want to reset hdfs write streaming task changes? (y/n)");

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            reset();
        }
        hdfsTaskService.done();

        paramsMap.put("lastState", new ConfigurationParam("lastState", false, "enriched_HDFSWriterStreamTask"));

        System.out.println(String.format("End configure the HDFS write task for %s", dataSourceName));
    }

    @Override
    public void apply() {

    }

    @Override
    public void reset() throws Exception {

    }
}
