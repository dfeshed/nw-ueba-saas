package fortscale.collection.hadoop;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HadoopInit implements InitializingBean{
	
	
	@Autowired
	private FileSystem hadoopFs;
	
	@Autowired
	protected ImpalaClient impalaClient;
	
	@Value("${hdfs.user.data.users.path}")
	private String impalaUsersDirectory;
	
	@Value("${impala.user.fields}")
	private String impalaUserFields;
	@Value("${impala.user.table.delimiter}")
	private String impalaUserTableDelimiter;
	@Value("${impala.user.table.name}")
	private String impalaUserTableName;
	
//	@Value("${}")
//	private String ;
	

	public void createDirectories() throws IOException{
		if(!hadoopFs.exists(new Path(impalaUsersDirectory))){
			hadoopFs.mkdirs(new Path(impalaUsersDirectory));
		}
	}
	
	public void createImpalaTables(){
		try{
			impalaClient.createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);
		} catch(Exception e){
			//Nothing to do. just making sure that the table exist.
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		createDirectories();
		
		createImpalaTables();
	}
}
