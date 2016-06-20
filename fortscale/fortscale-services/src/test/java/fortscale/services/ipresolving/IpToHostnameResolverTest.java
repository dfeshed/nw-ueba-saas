package fortscale.services.ipresolving;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.IseEvent;
import fortscale.services.ComputerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class IpToHostnameResolverTest {

	@Mock
	private ComputerLoginResolver computerLoginResolver;
	@Mock
	private DhcpResolver dhcpResolver;
	@Mock
	private IseResolver iseResolver;
	@Mock
	private DnsResolver dnsResolver;
	@Mock
	private StaticFileBasedMappingResolver fileResolver;
	@Mock
	private ComputerService computerService;
	@InjectMocks
	private IpToHostnameResolver resolver;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		// Create resolver metrics
		resolver.createMetrics();
		// set blacklist on resolver for tests
		resolver.setHostnameBlacklist("localhost*|my-pc|UNKNOWN-*");

		// set all providers enabled
		resolver.setDhcpProviderEnabled(true);
		resolver.setDnsProviderEnabled(true);
		resolver.setFileProviderEnabled(true);
		resolver.setLoginProviderEnabled(true);
	}

	@Test
	public void resolve_should_return_null_if_dns_name_not_in_ad_and_restrictADNames_is_true() {
		resolver.setDhcpProviderEnabled(false);
		resolver.setFileProviderEnabled(false);
		resolver.setLoginProviderEnabled(false);
		when(dnsResolver.getHostname("192.168.1.1", 155)).thenReturn("wowo");
		when(computerService.isHostnameInAD("wowo")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155L, true, true, false);
		assertNull(actual);
	}

	@Test
	public void resolve_should_return_null_if_dns_name_is_in_blacklist() {
		resolver.setDhcpProviderEnabled(false);
		resolver.setFileProviderEnabled(false);
		resolver.setLoginProviderEnabled(false);
		when(dnsResolver.getHostname("192.168.1.1", 155)).thenReturn("UNKNOWN-192-168-0-X.yahoo.com");
		when(computerService.isHostnameInAD("UNKNOWN-192-168-0-X.yahoo.com")).thenReturn(true);

		String actual = resolver.resolve("192.168.1.1", 155L, true, true, false);
		assertNull(actual);
	}

	@Test
	public void resolve_should_return_dns_name_is_dhcp_name_not_in_ad_and_restrictADNames_is_false() {
		resolver.setFileProviderEnabled(false);
		resolver.setLoginProviderEnabled(false);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		dhcpEvent.setTimestampepoch(100L);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(dnsResolver.getHostname("192.168.1.1", 155)).thenReturn("wowo");
		when(computerService.isHostnameInAD("WOWO")).thenReturn(true);
		when(computerService.isHostnameInAD("PC1")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155L, true, true, false);
		assertEquals("WOWO", actual);
	}

	@Test
	public void resolve_should_return_dns_name_is_dhcp_name_is_in_blacklist() {
		resolver.setFileProviderEnabled(false);
		resolver.setLoginProviderEnabled(false);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("localhost");
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(dnsResolver.getHostname("192.168.1.1", 155)).thenReturn("wowo");

		String actual = resolver.resolve("192.168.1.1", 155L, false, true, false);
		assertEquals("WOWO", actual);
	}

	@Test
	public void resolve_should_return_null_if_all_providers_are_disabled() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me-me");
		ComputerLoginEvent loginEvent = new ComputerLoginEvent();
		loginEvent.setHostname("pc1");
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(loginEvent);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(dnsResolver.getHostname("192.168.1.1", 155)).thenReturn("wowo");

		resolver.setDhcpProviderEnabled(false);
		resolver.setDnsProviderEnabled(false);
		resolver.setFileProviderEnabled(false);
		resolver.setLoginProviderEnabled(false);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertNull(actual);
	}

	@Test
	public void resolve_should_first_return_hostname_from_mapping_file_if_it_exists_there() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("pc1");

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1", actual);
	}

	@Test
	public void resolve_should_return_hostname_from_security_event_if_there_is_not_dhcp_events() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		ComputerLoginEvent loginEvent = new ComputerLoginEvent();
		loginEvent.setHostname("pc1");
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(loginEvent);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1", actual);
	}

	@Test
	public void resolve_should_not_return_hostname_from_security_event_if_there_is_a_dhcp_event_following_it() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		ComputerLoginEvent loginEvent = new ComputerLoginEvent();
		loginEvent.setHostname("pc1");
		loginEvent.setTimestampepoch(90L);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(loginEvent);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc2");
		dhcpEvent.setTimestampepoch(100L);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC2", actual);
	}

	@Test
	public void resolve_should_return_hostname_from_security_event_even_if_there_is_a_dhcp_event_with_the_same_hostname() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		ComputerLoginEvent loginEvent = new ComputerLoginEvent();
		loginEvent.setHostname("pc1");
		loginEvent.setTimestampepoch(100L);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(loginEvent);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		dhcpEvent.setTimestampepoch(100L);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1", actual);
	}

	@Test
	public void resolve_should_return_hostname_from_ise_event_even_if_there_is_a_dhcp_event_with_the_same_hostname() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		ComputerLoginEvent loginEvent = new ComputerLoginEvent();
		loginEvent.setHostname("pc1Login");
		loginEvent.setTimestampepoch(100L);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(loginEvent);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1Dhcp");
		dhcpEvent.setTimestampepoch(100L);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		resolver.setIseProviderEnabled(true);
		IseEvent iseEvent = new IseEvent();
		iseEvent.setHostname("pc1Ise");
		iseEvent.setTimestampepoch(100L);
		when(iseResolver.getLatestIseEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(iseEvent);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1LOGIN", actual);
	}

	@Test
	public void resolve_should_return_hostname_from_security_events_if_there_is_a_dhcp_event_that_is_older() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		ComputerLoginEvent loginEvent = new ComputerLoginEvent();
		loginEvent.setHostname("pc1");
		loginEvent.setTimestampepoch(110L);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(loginEvent);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc2");
		dhcpEvent.setTimestampepoch(100L);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1", actual);
	}

	@Test
	public void resolve_should_return_hostname_from_dhcp_only_if_it_is_in_ad_when_restrictToADName_is_true() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(null);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		dhcpEvent.setTimestampepoch(100L);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(computerService.isHostnameInAD("pc1")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155, true, true, false);
		assertNull(actual);
	}

	@Test
	public void resolve_should_return_any_hostname_from_dhcp_when_restrictToADName_is_false() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(null);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(computerService.isHostnameInAD("pc1")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1", actual);
	}

	@Test
	public void resolve_should_return_dns_name_when_dhcp_and_security_events_fail() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(null);
		when(dhcpResolver.getHostname("192.168.1.1", 155)).thenReturn(null);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(computerService.isHostnameInAD("pc1")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("PC1", actual);
	}

	@Test
	public void resolve_should_return_dns_names_only_if_it_is_in_ad_when_restrictToADName_is_true() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(null);
		when(dhcpResolver.getHostname("192.168.1.1", 155)).thenReturn(null);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("pc1");
		dhcpEvent.setTimestampepoch(100L);
		dhcpEvent.setAdHostName(false);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);
		when(computerService.isHostnameInAD("pc1")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155, true, true, false);
		assertNull(actual);
	}


	@Test
	public void resolve_should_not_return_hostname_from_dhcp_if_it_is_in_blacklist() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(null);
		DhcpEvent dhcpEvent = new DhcpEvent();
		dhcpEvent.setHostname("localhost");
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(dhcpEvent);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertNull(actual);
	}

	@Test
	public void resolve_should_not_return_hostname_from_dns_if_it_is_in_blacklist() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn(null);
		when(computerLoginResolver.getComputerLoginEvent("192.168.1.1", 155)).thenReturn(null);
		when(dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", 155)).thenReturn(null);
		when(dnsResolver.getHostname("192.168.1.1", 155)).thenReturn("localhost");
		when(computerService.isHostnameInAD("pc1")).thenReturn(false);

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertNull(actual);
	}

	@Test
	public void resolve_should_return_hostname_is_capital_letters() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me");

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("ME", actual);
	}

	@Test
	public void resolve_should_return_hostname_up_to_first_dot() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me.fortscale.dom");

		String actual = resolver.resolve("192.168.1.1", 155, false, true, false);
		assertEquals("ME", actual);
	}

	@Test
	public void resolve_should_return_hostname_as_full_dns_name() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me.fortscale.dom");

		String actual = resolver.resolve("192.168.1.1", 155, false, false, false);
		assertEquals("ME.FORTSCALE.DOM", actual);
	}

	@Test
	public void resolve_should_return_hostname_without_last_dot() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me.fortscale.dom.");

		String actual = resolver.resolve("192.168.1.1", 155, false, false, true);
		assertEquals("ME.FORTSCALE.DOM", actual);
	}

	@Test
	public void resolve_should_return_hostname_without_dots() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me.fortscale.dom");

		String actual = resolver.resolve("192.168.1.1", 155, false, true, true);
		assertEquals("ME", actual);
	}

	@Test
	public void resolve_should_return_hostname_without_dots_2() {
		when(fileResolver.getHostname("192.168.1.1")).thenReturn("me.fortscale.dom.");

		String actual = resolver.resolve("192.168.1.1", 155, false, true, true);
		assertEquals("ME", actual);
	}
}