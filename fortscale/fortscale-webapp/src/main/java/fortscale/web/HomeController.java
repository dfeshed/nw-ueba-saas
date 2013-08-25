package fortscale.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for the application home page.
 */
@RequestMapping("/")
@Controller
public class HomeController {
	
//	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping
    public String getIndexPage() {
        return "index";
    }
	
//	@RequestMapping("/views/main/header")
//    public String getMainHeaderPartialPage(ModelMap modelMap) {
//        return "main/header";
//    }
//	
//	@RequestMapping("/views/main/sidebar")
//    public String getMainSidebarPartialPage(ModelMap modelMap) {
//        return "main/sidebar";
//    }
//	
//	@RequestMapping("/views/dialogs/splunk_config")
//    public String getDialogsSplunkPartialPage(ModelMap modelMap) {
//        return "dialogs/splunk_config";
//    }
//	
//	@RequestMapping("/views/pages/main_dashboard")
//    public String getPagesMainDashboardPartialPage(ModelMap modelMap) {
//        return "pages/main_dashboard";
//    }
//	
//	@RequestMapping("/views/pages/dashboard")
//    public String getPagesDashboardPartialPage(ModelMap modelMap) {
//        return "pages/dashboard";
//    }
	
}
