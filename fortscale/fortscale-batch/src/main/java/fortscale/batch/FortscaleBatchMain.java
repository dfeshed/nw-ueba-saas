package fortscale.batch;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fortscale.utils.logging.Logger;




public class FortscaleBatchMain {
	private static Logger logger = Logger.getLogger(FortscaleBatchMain.class);
	
	// gather config files
	private static String[] configs = { "classpath*:META-INF/spring/fortscale-batch-context.xml" };

	
	
	private static final String RUN_FE = "runfe";
	private static final String UPDATE_AD_INFO = "updateAdInfo";
	private static final String UPDATE_AUTH_SCORE = "updateAuthScore";
	private static final String UPDATE_VPN_SCORE = "updateVpnScore";
	
	
	
	
	
	
	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			logger.error("no action recieved");
			throw new ParseException("wrong params");
		}
		if (args.length > 2) {
			logger.error("wrong number of parameters: {}", StringUtils.join(args, ','));
			throw new ParseException("wrong params");
		}
		
		String action = args[0];
		
		// create the app context
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configs);
		
		FortscaleBatch fortscaleBatch = (FortscaleBatch) ctx.getBean("fortscaleBatch");
		if(action.equalsIgnoreCase(RUN_FE)) {
			String userAdScoreCsvFileFullPathString=null;
			if(args.length > 1) {
				userAdScoreCsvFileFullPathString = args[1];
			}
			 
			fortscaleBatch.runfe(userAdScoreCsvFileFullPathString);
		} else if(action.equalsIgnoreCase(UPDATE_AD_INFO)) {
			fortscaleBatch.updateAdInfo();
		} else if(action.equalsIgnoreCase(UPDATE_AUTH_SCORE)) {
			fortscaleBatch.updateAuthScore();
		} else if(action.equalsIgnoreCase(UPDATE_VPN_SCORE)) {
			fortscaleBatch.updateVpnScore();
		} else {
			logger.error("no such action: {}.", action);
		}
	}
	
	
}
