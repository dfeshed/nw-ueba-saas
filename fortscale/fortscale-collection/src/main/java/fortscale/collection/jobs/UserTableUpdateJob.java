package fortscale.collection.jobs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.jobs.ad.AdProcessJob;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.hdfs.HDFSLineAppender;
import fortscale.utils.hdfs.split.DefaultFileSplitStrategy;
import fortscale.utils.logging.Logger;


@DisallowConcurrentExecution
public class UserTableUpdateJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileSystem hadoopFs;
	
	@Value("${impala.vpn.table.name}")
	private String vpnTableName;
	@Value("${impala.login.table.name}")
	private String loginTableName;
	@Value("${impala.ssh.table.name}")
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
	protected void runSteps() throws Exception {
		RemoteIterator<LocatedFileStatus> files = listOldFiles();
		
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
	
	private RemoteIterator<LocatedFileStatus> listOldFiles() throws IOException{
		startNewStep("listOldFiles");
		
		RemoteIterator<LocatedFileStatus> ret =  hadoopFs.listFiles(new Path(hadoopDirPath), false);
		
		finishStep();
		
		return ret;
	}
	
	private void deleteOldFiles(RemoteIterator<LocatedFileStatus> files) throws IOException{
		startNewStep("deleteOldFiles");
		
		while(files.hasNext()){
			LocatedFileStatus fileStatus = files.next();
			hadoopFs.delete(fileStatus.getPath(), true);
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
		for(String fieldDef: impalaUserFields.split(",")){
			String fieldDefSplit[] = fieldDef.split(" ");
			try {
				values.add(BeanUtils.getProperty(userTable, fieldDefSplit[0]));
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.warn(String.format("got the following exception while trying to read the field %s", fieldDef), e);
				values.add("NULL");
			}
		}
		
		usersAppender.writeLine(StringUtils.join(values, impalaUserTableDelimiter));
	}
		
	
	
	protected void refreshImpala() throws JobExecutionException {
		startNewStep("impala refresh");
		impalaClient.refreshTable(impalaTableName);
		finishStep();
	}

}
