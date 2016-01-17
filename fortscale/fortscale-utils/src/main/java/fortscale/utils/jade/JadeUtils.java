package fortscale.utils.jade;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.template.JadeTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Amir Kere on 17/01/16.
 */
public class JadeUtils {

	public String renderHTML(String jadePath, Map<String, Object> model) throws IOException {
		JadeTemplate template = Jade4J.getTemplate(jadePath);
		return Jade4J.render(template, model);
	}

}