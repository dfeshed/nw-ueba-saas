package fortscale.web;

import org.junit.Test;
import static org.junit.Assert.*;


import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.utils.test.category.IntegrationTestCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/applicationContext.xml"})
@Category(IntegrationTestCategory.class)
public class ContextTestInt {

	@Autowired
	private VpnDAO vpnDAO;
	
	@Autowired
	private AuthDAO loginDAO;
	
	@Autowired
	private AuthDAO sshDAO;
	
	@Value("${impala.score.ssh.table.name}")
	private String sshTableName;
	
	@Value("${impala.score.vpn.table.name}")
	private String vpnTableName;
	
	@Value("${impala.score.ldapauth.table.name}")
	private String loginTableName;
	
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
	}
}
