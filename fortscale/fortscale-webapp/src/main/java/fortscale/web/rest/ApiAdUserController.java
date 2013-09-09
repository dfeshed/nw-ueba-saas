package fortscale.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.activedirectory.main.ADManager;
import fortscale.activedirectory.qos.QoSBootstrapService;
import fortscale.activedirectory.qos.QoSManualTestService;
import fortscale.activedirectory.qos.QoSSanityService;
import fortscale.activedirectory.qos.QoSService;
import fortscale.services.fe.FeService;
import fortscale.utils.logging.Logger;




@Controller
@RequestMapping("/api/aduser/**")
public class ApiAdUserController {
	
	@Autowired
	private FeService feService;
	private QoSService qosService;
	private QoSSanityService qosSanityService;
	private QoSBootstrapService qosBootstrapService;
	private QoSManualTestService qosManualTestsService;

	private static final Logger logger = Logger.getLogger(ApiAdUserController.class);
	

//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runfe", method=RequestMethod.GET)
	@ResponseBody
	public String runfe(Model model){
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
		return "";
	}

	
//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runsanity", method=RequestMethod.GET)
	@ResponseBody
	public String runsanity(Model model) {
		String aggregateResult = "";
		int successRate = 0;
		int QOS_BOOTSTRAP_ITERATIONS = 100;
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
		return aggregateResult;
	}
	
	
//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runqos", method=RequestMethod.GET)
	@ResponseBody
	public String runqos(Model model){
		qosService = new QoSService(feService);
		ADManager adManager = new ADManager();
		adManager.run(qosService, null);
		return qosService.getQosResult();
	}
	

//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runqosbootstrap", method=RequestMethod.GET)
	@ResponseBody
	public String runqosbootstrap(Model model) {
		String aggregateResult = "";
		int successRate = 0;
		int QOS_BOOTSTRAP_ITERATIONS = 100;
		for (int i=0; i<QOS_BOOTSTRAP_ITERATIONS; i++) {
			logger.info("Running Test #{}", i);
			qosBootstrapService = new QoSBootstrapService(feService);
			ADManager adManager = new ADManager();
			adManager.run(qosBootstrapService, null);
			aggregateResult += qosBootstrapService.getQosResult() + "<BR>";
			successRate += qosBootstrapService.getQosSuccessRate();
			
		}
		successRate = successRate / QOS_BOOTSTRAP_ITERATIONS;
		
		aggregateResult += String.format("Automated Tests Result: %s%%" , successRate) ;
		return aggregateResult;
	}

	
	
//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runmanualtests", method=RequestMethod.GET)
	@ResponseBody
	public String runmanualtests(Model model) {
		String aggregateResult = "";
		int successRate = 0;
		int QOS_BOOTSTRAP_ITERATIONS = 100;
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
		return aggregateResult;
	}
	
	
}
