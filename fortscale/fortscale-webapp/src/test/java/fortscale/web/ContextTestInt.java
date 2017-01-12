package fortscale.web;

import fortscale.domain.fe.dao.AccessDAO;
import fortscale.utils.test.category.IntegrationTestCategory;
import fortscale.web.webconf.AdTaskConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/applicationContext.xml"})
@WebAppConfiguration( "classpath*:webapp-config.xml"  )
@Category(IntegrationTestCategory.class)
@Import(AdTaskConfig.class)
public class ContextTestInt {

	@Autowired
	private AccessDAO vpnDAO;
	
	@Autowired
	private AccessDAO loginDAO;
	
	@Autowired
	private AccessDAO sshDAO;
	
	@Value("${impala.score.ssh.table.name}")
	private String sshTableName;
	
	@Value("${impala.score.vpn.table.name}")
	private String vpnTableName;
	
	@Value("${impala.score.kerberos_logins.table.name}")
	private String loginTableName;
	
	@Value("${impala.data.table.fields.normalized_username}")
	public String normalizedUsername;
	
	@Test
	@Category(IntegrationTestCategory.class)
	public void testContext(){
		
		assertNotNull(loginDAO.getTableName());
		assertFalse(loginDAO.getTableName().isEmpty());
		assertEquals(loginTableName, loginDAO.getTableName());
		
		assertNotNull(vpnDAO.getTableName());
		assertFalse(vpnDAO.getTableName().isEmpty());
		assertEquals(vpnTableName, vpnDAO.getTableName());
		
		assertNotNull(sshDAO.getTableName());
		assertFalse(sshDAO.getTableName().isEmpty());
		assertEquals(sshTableName, sshDAO.getTableName());
		
		assertNotNull(sshDAO.NORMALIZED_USERNAME);
		assertFalse(sshDAO.NORMALIZED_USERNAME.isEmpty());
		assertEquals(normalizedUsername, sshDAO.NORMALIZED_USERNAME);
	}
}
