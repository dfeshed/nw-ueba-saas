package fortscale.collection.jobs;

import fortscale.collection.jobs.ad.AdProcessJob;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.hdfs.HDFSLineAppender;
import fortscale.utils.hdfs.split.DefaultFileSplitStrategy;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.ImpalaDateTime;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@DisallowConcurrentExecution
public class UserTableUpdateJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileSystem hadoopFs;
	
	@Value("${impala.score.vpn.table.name}")
	private String vpnTableName;
	@Value("${impala.score.ldapauth.table.name}")
	private String loginTableName;
	@Value("${impala.score.ssh.table.name}")
	private String sshTableName;
	@Value("${impala.user.fields}")
	private String impalaUserFields;
	@Value("${impala.user.table.delimiter}")
	private String impalaUserTableDelimiter;
	@Value("${users.page.size:10000}")
	private int pageSize;
	
	
	private HDFSLineAppender usersAppender;
		

	// job parameters:
	protected String hadoopDirPath;
	protected String hadoopFilename;
	protected String impalaTableName;
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		hadoopDirPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopDirPath");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");				
		String filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		hadoopFilename = String.format(filenameFormat, (new Date()).getTime()/1000);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 5;
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return false;
	}

	@Override
	protected void runSteps() throws Exception {
		List<Path> files = listOldFiles();
		
		try{
			
			createHdfsAppender();
			
			boolean isSucceeded = updateTable();
			if(!isSucceeded){
				return;
			}
			
		} finally{
			if(usersAppender != null){
				usersAppender.close();
			}
		}
		
		deleteOldFiles(files);
		
		refreshImpala();
		
	}
	
	private List<Path> listOldFiles() throws IOException{
		startNewStep("listOldFiles");
		List<Path> ret = new ArrayList<>();
		RemoteIterator<LocatedFileStatus> files =  hadoopFs.listFiles(new Path(hadoopDirPath), false);
		while(files.hasNext()){
			ret.add(files.next().getPath());
		}
		
		finishStep();
		
		return ret;
	}
	
	private void deleteOldFiles(List<Path> files){
		startNewStep("deleteOldFiles");
		
		for(Path path: files){
			try {
				hadoopFs.delete(path, true);
			} catch (IOException e) {
				String message = String.format("got an exception while trying to delete the old file %s. The exception: %s", path.getName(), e.toString());
				logger.warn(message, e);
				addWarn(message);
			}
		}
		
		
		finishStep();
	}
	
	private void createHdfsAppender() throws IOException{
		startNewStep("createHdfsAppender");
		
		DefaultFileSplitStrategy defaultFileSplitStrategy = new DefaultFileSplitStrategy();
		String fullPath = defaultFileSplitStrategy.getFilePath(hadoopDirPath, hadoopFilename, 0);
		usersAppender = new HDFSLineAppender();
		usersAppender.open(fullPath);
		
		finishStep();
	}
	
	private boolean updateTable() throws IOException{
		startNewStep("update");
		
		pageSize = 10000;
		
		int numOfPages = (int) (((userRepository.count() -1) / pageSize) + 1); 
		for(int i = 0; i < numOfPages; i++){
			logger.info("retrieving page #{} of user documents. page size is {}.", i, pageSize);
			PageRequest pageRequest = new PageRequest(i, pageSize);
			logger.info("writing all the users in this page to the user table in the hdfs");
			for(User user: userRepository.findAll(pageRequest).getContent()){
				processUser(user);
			}
			logger.info("finished writing all the users in this page to the user table in the hdfs");
		}
		
		finishStep();
		
		return true;
	}
	
	private void processUser(User user) throws IOException{
		User manager = null;
		if(user.getAdInfo().getManagerDN() != null){
			manager = userRepository.findByAdInfoDn(user.getAdInfo().getManagerDN());
		}
		UserTable userTable = new UserTable(user, manager);
		userTable.setSecUsernames(user.getLogUserName(loginTableName));
		userTable.setSshUsernames(user.getLogUserName(sshTableName));
		userTable.setVpnUsernames(user.getLogUserName(vpnTableName));
		
		List<String> values = new ArrayList<>();
		HashMap<String, Class<?>> impalaUserFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaUserFields);
		for(String fieldDef: ImpalaParser.getTableFieldNames(impalaUserFields)){
			try {
				String val = BeanUtils.getProperty(userTable, fieldDef);
				if(StringUtils.isEmpty(val)){
					val = ImpalaParser.IMPALA_NULL_VALUE;
				} else if(impalaUserFieldsMap.get(fieldDef).equals(ImpalaDateTime.class)){
					val = ImpalaDateTime.formatTimeDate(new DateTime(Long.parseLong(val), DateTimeZone.UTC));
				}
				values.add(val);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NumberFormatException e) {
				logger.warn(String.format("got the following exception while trying to read the field %s", fieldDef), e);
				values.add(ImpalaParser.IMPALA_NULL_VALUE);
			}
		}
		
		usersAppender.writeLine(StringUtils.join(values, impalaUserTableDelimiter));
	}
		
	
	
	protected void refreshImpala() throws Exception {
		startNewStep("impala refresh");
		impalaClient.refreshTable(impalaTableName);
		finishStep();
	}

}
