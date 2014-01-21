package fortscale.collection;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * read xml file containing the configuration of the jobs to chain
 */
public class JobChainingListener extends JobListenerSupport {

	private static Logger logger = LoggerFactory.getLogger(JobChainingListener.class);

	private List<Chain> chains = new LinkedList<Chain>();
	
	public JobChainingListener(String xmlPath) throws Exception {
		if (xmlPath==null || xmlPath.length()==0)
			throw new IllegalArgumentException("xml path is required");
		
		File file = new File(xmlPath);
		if (!file.exists() || !file.canRead())
			throw new IllegalArgumentException("xml file " + xmlPath + "is not accesible");
		
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		buildChains(doc);
	}
	
	public String getName() {
		return "JobChainingListener";
	}
	
	@Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		// do not chain jobs on error
		if (jobException!=null)
			return;
		
		JobKey job = context.getJobDetail().getKey();
		// get dependent jobs upon this one
		for (Chain chain : chains) {
			if (chain.first.equals(job)) {
				// trigger the second job
				try {
					logger.info("Job '{}' will now chain to Job '{}'", chain.first, chain.second);
					context.getScheduler().triggerJob(chain.second);
				} catch (SchedulerException e) {
					logger.error(String.format("Error encountered during chaining to Job '%s'", chain.second), e);
				}
			}
		}
	}
	
	private void buildChains(Document doc) {
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("chain");
		for (int i=0;i<nList.getLength();i++) {
			Element node = (Element)nList.item(i);
				
			// get job keys
			JobKey firstJobKey = getJobKey(getSingleElementByTagName(node, "first"));
			JobKey secondJobKey = getJobKey(getSingleElementByTagName(node, "second"));
			
			if (firstJobKey==null || secondJobKey==null) {
				logger.warn("skiping malformed chain element " + node.toString());
				continue;
			}
			
			logger.debug("adding chain from {} to {}", firstJobKey, secondJobKey);
			chains.add(new Chain(firstJobKey, secondJobKey));
		}
	}
	
	private JobKey getJobKey(Element node) {
		if (node==null)
			return null;
		
		String name = getElementText(node, "name");
		String group = getElementText(node, "group");
		
		return new JobKey(name, group);
	}
	
	private Element getSingleElementByTagName(Element parent, String tagName) {
		NodeList list = parent.getElementsByTagName(tagName);
		if (list.getLength()!=1)
			return null;
		
		return (Element)list.item(0);
	}
	
	private String getElementText(Element parent, String tagName) {
		Element child = getSingleElementByTagName(parent, tagName);
		if (child==null)
			return null;
		return child.getTextContent();
	}

	
	class Chain {
		public Chain(JobKey first, JobKey second) {
			this.first = first;
			this.second = second;
		}
		
		public JobKey first;
		public JobKey second;
	}
	
}
