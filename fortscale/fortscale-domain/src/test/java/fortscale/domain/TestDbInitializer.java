package fortscale.domain;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.impl.UserMachineDAOImpl;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.impl.AuthDAOImpl;


@Component
public class TestDbInitializer {
	
	private static final File AD_USER_COLLECTION_DATA_JSON_FILE = getFile("src/test/data/mongodb/ad_user.json");
	private static final File AD_GROUP_COLLECTION_DATA_JSON_FILE = getFile("src/test/data/mongodb/ad_group.json");
	private static final File AD_FE_COLLECTION_DATA_JSON_FILE = getFile("src/test/data/mongodb/ad_user_features_extraction.json");
	private static final File USER_COLLECTION_DATA_JSON_FILE = getFile("src/test/data/mongodb/user.json");
	private static final String USER_TO_HOST_NAME_INPUT_FILE_NAME = "src/test/data/impala/usertohostname.csv";
	private static final String AUTHENTICATON_SCORE_INPUT_FILE_NAME = "src/test/data/impala/authenticationscores.csv";
	
	private static File getFile(String path) {
		File file = new File(path.replace("/", "\\"));
		return file;
	}
	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private AdGroupRepository adGroupRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMachineDAOImpl userMachineDAO;
	
	@Autowired
	private AuthDAOImpl authDAO;
	
//	@Autowired
//	private JdbcOperations hiveJdbcTemplate;
	

	@Value("${mongo.db.name}")
	private String mongoDbName;
	
	@Value("${db.usernametohostname.hadoopInputfile}")
	private String userMachineHadoopInputFileName;
	
	@Value("${db.authenticationscores.hadoopInputfile}")
	private String authenticationscoresHadoopInputFileName;
	
	private boolean isInit = false;


	public void init() throws IOException, InterruptedException {
		if(!isInit) {
			copyCsvToHadoop(USER_TO_HOST_NAME_INPUT_FILE_NAME, userMachineHadoopInputFileName);
			userMachineDAO.setTableName(String.format("%stest", userMachineDAO.getTableName()));
			userMachineDAO.createTable(userMachineHadoopInputFileName);
			
			copyCsvToHadoop(AUTHENTICATON_SCORE_INPUT_FILE_NAME, authenticationscoresHadoopInputFileName);
			authDAO.setTableName(String.format("%stest", authDAO.getTableName()));
			authDAO.createTable(authenticationscoresHadoopInputFileName);
			
			
			ObjectMapper mapper = new ObjectMapper();
			
			
			//Filling ad_group collection with data.
			adGroupRepository.deleteAll();
			List<AdGroup> adGroups = mapper.readValue(AD_GROUP_COLLECTION_DATA_JSON_FILE, new TypeReference<List<AdGroup>>() {});
			adGroupRepository.save(adGroups);
			
			
			//Filling ad_user collection with data.
			adUserRepository.deleteAll();
			List<AdUser> adUsers = mapper.readValue(AD_USER_COLLECTION_DATA_JSON_FILE, new TypeReference<List<AdUser>>() {});
			adUserRepository.save(adUsers);
			
			//Filling ad_user_features_extraction collection with data.
			adUsersFeaturesExtractionRepository.deleteAll();
			List<AdUserFeaturesExtraction> adUserFeaturesExtractions = mapper.readValue(AD_FE_COLLECTION_DATA_JSON_FILE, new TypeReference<List<AdUserFeaturesExtraction>>() {});
			for(AdUserFeaturesExtraction adUserFeaturesExtraction: adUserFeaturesExtractions) {
				adUsersFeaturesExtractionRepository.saveMap(adUserFeaturesExtraction);
			}
			
			//Filling ad_user collection with data.
			userRepository.deleteAll();
			List<User> users = mapper.readValue(USER_COLLECTION_DATA_JSON_FILE, new TypeReference<List<User>>() {});
			userRepository.save(users);
			
			isInit = true;
		}
	}
	
	private void copyCsvToHadoop(String localCsvFullPath, String hadoopCsvDataInputFullPath){
		Runtime run = Runtime.getRuntime();

		Process pr;

		String hadoopCmd = "hadoop fs -copyFromLocal "
				+ localCsvFullPath + " " + hadoopCsvDataInputFullPath;
//		logger.info("copying local file to hadoop with the following shell command: {}", hadoopCmd);
		try {
			pr = run.exec(hadoopCmd);
		
			pr.waitFor();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void createTable(String inputFile) {
//		String sql = String.format("drop table %s",userMachineDAO.getTableName());
//		hiveJdbcTemplate.execute(sql);
//		sql = String.format("create table if not exists %s (username string, hostname string, logoncount bigint, lastlogon timestamp, hostnameip string) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'",userMachineDAO.getTableName());
//		hiveJdbcTemplate.execute(sql);
//		sql = String.format("load data local inpath '%s' into table %s", inputFile, userMachineDAO.getTableName());
//		hiveJdbcTemplate.execute(sql);
//	}
	
}
