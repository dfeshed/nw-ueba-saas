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
  'ctxhub.domain_is_whitelisted': 'domainIsWhitelisted',
  'ctxhub.domain_is_not_whitelisted': 'domainIsNotWhitelisted',
  'smooth.smooth_beaconing_score': 'uniformTimeIntervals',
  'new_domain.age_score': 'newDomainToEnvironment',
  'whois.age_score': 'newDomainRegistration',
  'whois.validity_score': 'domainRegistrationExpires',
  'domain.referer_score': 'rareDomainInEnvironment',
  'domain.referer_ratio_score': 'noReferers',
  'domain.ua_ratio_score': 'rareUserAgent'
};

Object.keys(keys).forEach((key) => {
  const was = keys[key];
  keys[key] = `${i18nPath}${was}`;
});

export default keys;
