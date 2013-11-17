package fortscale.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fortscale.utils.logging.annotation.LogException;

/**
 * Handles requests for the application home page.
 */
@RequestMapping("/")
@Controller
public class HomeController extends BaseController{
	
//	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping("/")
	@LogException
	public void unmappedRequest(HttpServletRequest request,
			HttpServletResponse response) {
        try {
        	if(isThisAnalystAuth()) {
        		response.sendRedirect("index.html");
        	} else {
        		response.sendRedirect("signin.html");
        	}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
