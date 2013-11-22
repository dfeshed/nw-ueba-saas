package fortscale.activedirectory.main;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.activedirectory.qos.QoSAutoTestService;
import fortscale.activedirectory.qos.QoSManualTestService;
import fortscale.activedirectory.qos.QoSSanityService;
import fortscale.activedirectory.qos.QoSService;
import fortscale.services.fe.FeService;
import fortscale.utils.logging.Logger;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/active-directory-application-context-test.xml" })
@Ignore
public class ADManagerTest {

	@Autowired
	private FeService feService;
	private QoSService qosService;
	private QoSSanityService qosSanityService;
	private QoSAutoTestService qosBootstrapService;
	private QoSManualTestService qosManualTestsService;

	private final int QOS_BOOTSTRAP_ITERATIONS = 1;
	
	private static final Logger logger = Logger.getLogger(ADManagerTest.class);

	 @Test
	 public void thisAlwaysPasses() {
	 }
	 
	@Ignore
	public void testFe() {
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
	}

	
	@Ignore
	public void testQoS() {
		qosService = new QoSService(feService);
		ADManager adManager = new ADManager();
		adManager.run(qosService, null);
		logger.info(qosService.getQosResult());
	}
		

	@Ignore
	public void testQoSSanity() {
		String aggregateResult = "";
		int successRate = 0;
		for (int i=0; i<QOS_BOOTSTRAP_ITERATIONS; i++) {
			logger.info("Running Test #{}", i);
			qosSanityService = new QoSSanityService(feService);
			ADManager adManager = new ADManager();
			adManager.run(qosSanityService, null);
			aggregateResult += qosSanityService.getQosResult() + "<BR>";
			successRate += qosSanityService.getQosSuccessRate();
		}
		successRate = successRate / QOS_BOOTSTRAP_ITERATIONS;
		
		aggregateResult += String.format("Sanity Tests Result: %s%%" , successRate) ;
		logger.info(aggregateResult);
	}
		

	@Ignore
	public void testQoSAuto() {
		String aggregateResult = "";
		int successRate = 0;
		for (int i=0; i<QOS_BOOTSTRAP_ITERATIONS; i++) {
			logger.info("Running Test #{}", i);
			qosBootstrapService = new QoSAutoTestService(feService);
			ADManager adManager = new ADManager();
			adManager.run(qosBootstrapService, null);
			aggregateResult += qosBootstrapService.getQosResult() + "<BR>";
			successRate += qosBootstrapService.getQosSuccessRate();
				
		}
		successRate = successRate / QOS_BOOTSTRAP_ITERATIONS;
			
		aggregateResult += String.format("Automated Tests Result: %s%%" , successRate) ;
		logger.info(aggregateResult);
	}

	
	@Ignore
	public void testQoSManual() {
		String aggregateResult = "";
		int successRate = 0;
		for (int i=0; i<QOS_BOOTSTRAP_ITERATIONS; i++) {
			logger.info("Running Test #{}", i);
			qosManualTestsService = new QoSManualTestService(feService);
			ADManager adManager = new ADManager();
			adManager.run(qosManualTestsService, null);
			aggregateResult += qosManualTestsService.getQosResult() + "<BR>";
			successRate += qosManualTestsService.getQosSuccessRate();
			
		}
		successRate = successRate / QOS_BOOTSTRAP_ITERATIONS;
			
		aggregateResult += String.format("Manual Tests Result: %s%%" , successRate) ;
		logger.info(aggregateResult);
	}
		
}



