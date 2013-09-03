package fortscale.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.activedirectory.main.ADManager;
import fortscale.activedirectory.qos.QoSBootstrapService;
import fortscale.activedirectory.qos.QoSService;
import fortscale.services.fe.FeService;




@Controller
@RequestMapping("/api/aduser/**")
public class ApiAdUserController {
	
	@Autowired
	private FeService feService;
	private QoSService qosService;
	private QoSBootstrapService qosBootstrapService;

//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runfe", method=RequestMethod.GET)
	@ResponseBody
	public String runfe(Model model){
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
		return "";
	}
	
	
//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runqos", method=RequestMethod.GET)
	@ResponseBody
	public String runqos(Model model){
		qosService = new QoSService(feService);
		ADManager adManager = new ADManager();
		adManager.run(qosService, null);
		return "";
	}
	

//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runqosbootstrap", method=RequestMethod.GET)
	@ResponseBody
	public String runqosbootstrap(Model model){
		qosBootstrapService = new QoSBootstrapService(feService);
		ADManager adManager = new ADManager();
		adManager.run(qosBootstrapService, null);
		return "";
	}
	

}
