package fortscale.common.dataqueries;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringValueResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataentity.DataEntityField;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.utils.TreeNode;
import fortscale.utils.hdfs.partition.PartitionStrategy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/fortscale-common-context-dataEntity-test.xml"})
public class DataEntitiesConfigTest  implements EmbeddedValueResolverAware {


    public static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    public static String joinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"left\":{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"users\",\"field\":\"normalized_username\"}}],\"sort\":[],\"limit\":50,\"offset\":0}";

    protected DataQueryDTO dataQueryDTO1;
    protected DataQueryDTO joinDTO;
    protected ObjectMapper mapper = new ObjectMapper();

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.stringValueResolver = resolver;
	}

    protected StringValueResolver stringValueResolver;

    DataEntitiesConfig dataEntitiesConfig = new DataEntitiesConfig();

	protected List<TreeNode<DataEntity>> entityTrees = new ArrayList<>();



    @Before
    public void setUp() throws Exception {


        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTOImpl.class);
        joinDTO = mapper.readValue(joinDTOJson, DataQueryDTOImpl.class);
		dataEntitiesConfig.setEmbeddedValueResolver(stringValueResolver);

		/*
        stringValueResolver = Mockito.mock(StringValueResolver.class);
        dataEntitiesConfig.setEmbeddedValueResolver(stringValueResolver);

        String entityToTest = "kerberos_logins";
		String secEntity = entityToTest;
		String loginEntity = "logins";
		String sshEntity = "ssh" ;
		String vpnEntity = "vpn";
		String vpnSessionEntity = "vpn_session";
		String userEntity = "users";
		String authEvent = "auth_event";
		String accessEvent="access_event";
		String sessionEvent ="session";
		String scoredEvents = "scored_access_event";
		String AcessEventsFields = "type, username, normalized_username, source_ip, source_machine, status, event_time, event_time_utc";



        String entityToTestFields = "source_machine_type, destination_machine_type, failure_code";
        Mockito.when(stringValueResolver.resolveStringValue("entities.kerberos_logins.field.source_machine_type.type")).thenReturn("STRING");

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities." + entityToTest + ".fields" + "}")).thenReturn(entityToTestFields);
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities" + "}")).thenReturn(secEntity+","+loginEntity+","+","+sshEntity+","+vpnEntity+","+vpnSessionEntity+","+userEntity);
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.short_name" + "}")).thenReturn("Kerberos");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.logins.short_name" + "}")).thenReturn("Login");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.default_sort" + "}")).thenReturn("destination_machine_type ASC");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.source_machine_type.name" + "}")).thenReturn("Source Computer Type");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.source_machine_type.type" + "}")).thenReturn("STRING");

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.destination_machine_type.name" + "}")).thenReturn("Destination Computer Type");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.destination_machine_type.type" + "}")).thenReturn("STRING");

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.failure_code.name" + "}")).thenReturn("Error Code");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.failure_code.type" + "}")).thenReturn("STRING");

        //for entity partition test
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.partitions" + "}")).thenReturn("daily");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.username.column" + "}")).thenReturn("account_name");

*/
		// create the expected entity tree based on the entities-test.properties

		DataEntity de1 = new DataEntity();
		de1.setId("access_event");
		DataEntity de2 = new DataEntity();
		de2.setId("session");
		DataEntity de3 = new DataEntity();
		de3.setId("scored_access_event");
		TreeNode<DataEntity> tn1 = new TreeNode<>(de3);
		DataEntity de4 = new DataEntity();
		de4.setId("auth_event");
		TreeNode<DataEntity> tn2 = new TreeNode<>(de4);
		DataEntity de5 = new DataEntity();
		de5.setId("kerberos_logins");
		TreeNode<DataEntity> tn3 = new TreeNode<>(de5);
		DataEntity de6 = new DataEntity();
		de6.setId("logins");
		TreeNode<DataEntity> tn4= new TreeNode<>(de6);
		DataEntity de7 = new DataEntity();
		de7.setId("ssh");
		TreeNode<DataEntity> tn5 = new TreeNode<>(de7);
		DataEntity de8 = new DataEntity();
		de8.setId("vpn");
		TreeNode<DataEntity> tn6 = new TreeNode<>(de8);
		DataEntity de9 = new DataEntity();
		de9.setId("vpn_session");
		TreeNode<DataEntity> tn7 = new TreeNode<>(de9);
		DataEntity de10 = new DataEntity();
		de10.setId("users");

		TreeNode<DataEntity> root1 = new TreeNode<DataEntity>(de1);
		TreeNode<DataEntity> root2 = new TreeNode<DataEntity>(de2);
		TreeNode<DataEntity> root3 = new TreeNode<DataEntity>(de10);



		tn1.setParent(root1);
		tn2.setParent(tn1);
		tn3.setParent(tn2);
		tn4.setParent(root1);
		tn5.setParent(tn2);
		tn6.setParent(tn1);
		tn7.setParent(root2);

		root2.setChaild(tn7);
		root1.setChaild(tn1);
		root1.setChaild(tn4);
		tn1.setChaild(tn2);
		tn1.setChaild(tn6);
		tn2.setChaild(tn3);
		tn2.setChaild(tn5);

		entityTrees.add(root1);
		entityTrees.add(root2);
		entityTrees.add(root3);


		/*
		//Mocking for tree creation
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+secEntity+".extends" + "}")).thenReturn(authEvent);
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+loginEntity+".extends" + "}")).thenReturn(accessEvent);
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+sshEntity+".extends" + "}")).thenReturn(authEvent);
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+vpnEntity+".extends" + "}")).thenReturn(scoredEvents);
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+vpnSessionEntity+".extends" + "}")).thenReturn(sessionEvent);
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+scoredEvents+".extends" + "}")).thenReturn(accessEvent);
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+authEvent+".extends" + "}")).thenReturn(scoredEvents);

		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+accessEvent+".default_sort" + "}")).thenReturn("event_time DESC");
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+sessionEvent+".default_sort" + "}")).thenReturn("start_time DESC");
		Mockito.when(stringValueResolver.resolveStringValue("${" + "entities."+sessionEvent+".default_sort" + "}")).thenReturn("start_time DESC");
		*/



    }

    @Test
    public void testGetAllEntityFields() throws Exception {
        List<String> arr = dataEntitiesConfig.getAllEntityFields("kerberos_logins");

        String listString="";
        for (String s : arr){ listString += s + ", ";}
        assertEquals("Get all entity fields of kerberos_logins", "source_machine_type, destination_machine_type, failure_code, source_machine_score, destination_machine, destination_machine_score, is_from_vpn, is_sensitive_machine, event_time_score, event_score, severity, type, username, normalized_username, source_ip, source_machine, status, event_time, event_time_utc, ",listString);
    }

    @Test
    public void testGetAllLogicalEntities() throws Exception {
        List<DataEntity> arr = dataEntitiesConfig.getAllLogicalEntities();
		Map<String,DataEntity> entitiesMap = new HashMap<>();
		for (DataEntity dataEntity : arr)
		{
			entitiesMap.put(dataEntity.getId(),dataEntity);
		}

        String listString="";
        for (DataEntityField field : entitiesMap.get("kerberos_logins").getFields()){ listString += field.getId() + ", ";}
        assertEquals("SQL Select Part for DTO1" ,"username, type, normalized_username, source_ip, source_machine_type, source_machine, destination_machine_type, destination_machine, failure_code, status, is_sensitive_machine, is_from_vpn, event_time, event_time_utc, event_score, event_time_score, source_machine_score, destination_machine_score, severity, ",listString );
    }

    @Test
    public void testGetLogicalEntity() throws Exception {
        DataEntity entity = dataEntitiesConfig.getLogicalEntity("kerberos_logins");
        String listString="";
        for (DataEntityField field : entity.getFields()){ listString += field.getId() + ", ";}
        assertEquals("SQL Select Part for kerberos_logins DTO" , "username, type, normalized_username, source_ip, source_machine_type, source_machine, destination_machine_type, destination_machine, failure_code, status, is_sensitive_machine, is_from_vpn, event_time, event_time_utc, event_score, event_time_score, source_machine_score, destination_machine_score, severity, ",listString);
    }

    @Test
    public void getEntityPartitions() throws Exception {
        PartitionStrategy partition = dataEntitiesConfig.getEntityPartitionStrategy("kerberos_logins");
        assertEquals("partition.entity_field" , "yearmonthday",partition.getImpalaPartitionFieldName());
    }

    @Test
    public void testGetFieldColumn() throws Exception {
        String entity = dataEntitiesConfig.getFieldColumn("kerberos_logins", "username");
        assertEquals("Get value of a column from impala" , "account_name", entity);
    }


	@Test
	public void testGetEntitiesTrees() throws Exception{

		//String val = this.stringValueResolver.resolveStringValue("${entities.access_event.extends}");
		List<TreeNode<DataEntity>> result = dataEntitiesConfig.getEntitiesTrees();
		int countOfEqual=0;


		for (TreeNode<DataEntity> expTree : entityTrees)
		{
			if (isTreeExist(expTree,result))
				countOfEqual++;


		}

		assertEquals(countOfEqual,result.size());

	}

	private boolean isTreeExist(TreeNode<DataEntity> expTree ,List<TreeNode<DataEntity>> actualTrees)
	{
		for (TreeNode<DataEntity> actualTree : actualTrees)
		{
			if (isTheSameTrees(expTree,actualTree))
				return true;
		}
		return false;
	}


	private boolean isTheSameTrees(TreeNode<DataEntity> tree1 , TreeNode<DataEntity> tree2)
	{
		boolean isSame = false;
		if ((tree1.getParent() != null && tree2.getParent()!= null && tree1.getParent().getData().equals(tree2.getParent().getData()) || tree1.getParent() == tree2.getParent())
				&& tree1.getChildrens().size()==0 && tree2.getChildrens().size()==0
				&& tree1.getData().equals(tree2.getData()))
			return true;

		if ((tree1.getParent() != null && tree2.getParent()!= null  && !tree1.getParent().getData().equals(tree2.getParent().getData()) )
				|| tree1.getChildrens().size() != tree2.getChildrens().size()
				|| !tree1.getData().equals(tree2.getData()))
			return false;

		for (TreeNode<DataEntity> tn : tree1.getChildrens() )
		{
			isSame =  isTreeExist(tn,tree2.getChildrens());
		}

		return isSame;

	}


}
