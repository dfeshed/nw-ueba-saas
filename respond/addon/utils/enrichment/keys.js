/**
 * Dictionary of enrichment keys that map to an i18n string.
 *
 * This object is a hashtable whose keys are enrichment keys that originate from the backend. The enrichment keys
 * are essentially extra properties that get assigned to event objects by our enrichment models.
 *
 * The values of the hash keys in this object are the paths of the corresponding i18n strings that describe the
 * enrichment keys. Not all enrichment keys get mapped to i18n strings; only those we wish to display in the UI.
 *
 * Note that sometimes 2 keys can map to the same i18n string, because the key names can depend on the enrichment
 * source (e.g., packet vs. log) but the i18n string may be independent of the source.
 *
 * @type {object}
 * @public
 */
const i18nPath = 'respond.enrichment.';

const keys = {
  'rsa_analytics_http-log_c2_smooth_score': 'uniformTimeIntervals',
  'rsa_analytics_http-log_c2_newdomain_score': 'newDomainToEnvironment',
  'rsa_analytics_http-log_c2_referer_score': 'rareDomainInEnvironment',
  'rsa_analytics_http-log_c2_whois_age_score': 'newDomainRegistration',
  'rsa_analytics_http-log_c2_whois_validity_score': 'domainRegistrationExpires',
  'rsa_analytics_http-log_c2_ua_ratio_score': 'rareUserAgent',
  'rsa_analytics_http-log_c2_referer_ratio_score': 'noReferers',
  // packet
  'rsa_analytics_http-packet_c2_smooth_score': 'uniformTimeIntervals',
  'rsa_analytics_http-packet_c2_newdomain_score': 'newDomainToEnvironment',
  'rsa_analytics_http-packet_c2_referer_score': 'rareDomainInEnvironment',
  'rsa_analytics_http-packet_c2_whois_age_score': 'newDomainRegistration',
  'rsa_analytics_http-packet_c2_whois_validity_score': 'domainRegistrationExpires',
  'rsa_analytics_http-packet_c2_ua_ratio_score': 'rareUserAgent',
  'rsa_analytics_http-packet_c2_referer_ratio_score': 'noReferers',
  // win auth
  'rsa_analytics_uba_winauth_highserverscore_score': 'highNumberServersAccessed',
  'rsa_analytics_uba_winauth_newserverscore_score': 'highNumberNewServersAccessed',
  'rsa_analytics_uba_winauth_newdevicescore_score': 'highNumberNewDevicesAccessed',
  'rsa_analytics_uba_winauth_failedserversscore_score': 'highNumberFailedLogins',
  'rsa_analytics_uba_winauth_newdeviceservice_score': 'passTheHash',
  'rsa_analytics_uba_winauth_logontypescore_score': 'rareLogonType',
  // vpn
  'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score': 'authFromRareDevice',
  'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score': 'authFromRareLocation',
  'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score': 'authFromRareServiceProvider',
  'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score': 'authFromNewServiceProvider',
  'rsa_analytics_uba-cisco_vpn_smoothloginfailuresscore_score': 'highNumberVPNFailedLogins'
};

Object.keys(keys).forEach((key) => {
  const was = keys[key];
  keys[key] = `${i18nPath}${was}`;
});

export default keys;
