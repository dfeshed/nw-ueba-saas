package fortscale.utils.jade;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.template.JadeTemplate;
import fortscale.utils.logging.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Amir Kere on 17/01/16.
 */
public class JadeUtils {

	private static Logger logger = Logger.getLogger(JadeUtils.class);

	public String renderHTML(String jadePath, Map<String, Object> model) throws IOException {
		logger.info("rendering HTML from {}", jadePath);
		JadeTemplate template = Jade4J.getTemplate(jadePath);
		String result = Jade4J.render(template, model);
		logger.debug("rendered html is - {}", result);
		return result;
	}

}