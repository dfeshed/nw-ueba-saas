// List of default enrichment keys to display in the DOM for a storyline item.
// This is just the list of all the possible keys we would want to display, if we have the data. Most storyline items
// will not have data for all of these keys (or even half of them).
export default [
  'rsa_analytics_http-log_c2_smooth_score',
  'rsa_analytics_http-log_c2_newdomain_score',
  'rsa_analytics_http-log_c2_referer_score',
  'rsa_analytics_http-log_c2_whois_age_score',
  'rsa_analytics_http-log_c2_whois_validity_score',
  'rsa_analytics_http-log_c2_ua_ratio_score',
  'rsa_analytics_http-log_c2_referer_ratio_score',
  'rsa_analytics_http-packet_c2_smooth_score',
  'rsa_analytics_http-packet_c2_newdomain_score',
  'rsa_analytics_http-packet_c2_referer_score',
  'rsa_analytics_http-packet_c2_whois_age_score',
  'rsa_analytics_http-packet_c2_whois_validity_score',
  'rsa_analytics_http-packet_c2_ua_ratio_score',
  'rsa_analytics_http-packet_c2_referer_ratio_score',
  'rsa_analytics_uba_winauth_highserverscore_score',
  'rsa_analytics_uba_winauth_newserverscore_score',
  'rsa_analytics_uba_winauth_newdevicescore_score',
  'rsa_analytics_uba_winauth_failedserversscore_score',
  'rsa_analytics_uba_winauth_newdeviceservice_score',
  'rsa_analytics_uba_winauth_logontypescore_score',
  'rsa_analytics_uba-cisco_vpn_smoothrarehostscore_score',
  'rsa_analytics_uba-cisco_vpn_smoothrarelocationscore_score',
  'rsa_analytics_uba-cisco_vpn_smoothrareispscore_score',
  'rsa_analytics_uba-cisco_vpn_smoothnewispscore_score',
  'rsa_analytics_uba-cisco_vpn_smoothloginfailuresscore_score'
];
