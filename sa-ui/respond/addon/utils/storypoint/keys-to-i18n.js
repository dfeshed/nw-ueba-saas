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
  // C2 packet model enrichments
  'http-packet.c2.contexthub_whitelist_enrich.domain_is_whitelisted': 'domainIsWhitelisted',
  'http-packet.c2.contexthub_whitelist_enrich.domain_is_not_whitelisted': 'domainIsNotWhitelisted',
  'http-packet.c2.smooth.score': 'uniformTimeIntervals',
  'http-packet.c2.newdomain.score': 'newDomainToEnvironment',
  'http-packet.c2.whois.age_scoreNetWitness': 'newDomainRegistration',
  'http-packet.c2.whois.validity_scoreNetWitness': 'domainRegistrationExpires',
  'http-packet.c2.referer.score': 'rareDomainInEnvironment',
  'http-packet.c2.referer.ratio_score': 'noReferers',
  'http-packet.c2.ua.ratio_score': 'rareUserAgent',

  // C2 log model enrichments:
  'http-log.c2.contexthub_whitelist_enrich.domain_is_whitelisted': 'domainIsWhitelisted',
  'http-log.c2.contexthub_whitelist_enrich.domain_is_not_whitelisted': 'domainIsNotWhitelisted',
  'http-log.c2.smooth.score': 'uniformTimeIntervals',
  'http-log.c2.newdomain.score': 'newDomainToEnvironment',
  'http-log.c2.whois.age_scoreNetWitness': 'newDomainRegistration',
  'http-log.c2.whois.validity_scoreNetWitness': 'domainRegistrationExpires',
  'http-log.c2.referer.score': 'rareDomainInEnvironment',
  'http-log.c2.referer.ratio_score': 'noReferers',
  'http-log.c2.ua.ratio_score': 'rareUserAgent'
};

Object.keys(keys).forEach((key) => {
  const was = keys[key];
  keys[key] = `${i18nPath}${was}`;
});

export default keys;
