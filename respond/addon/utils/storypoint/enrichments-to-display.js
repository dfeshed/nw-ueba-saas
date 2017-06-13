/**
 * Ordered list of possible enrichments to be displayed (if given) for each storypoint.
 * @public
 */
export default [
  {
    dataKey: 'ctxhub.domain_is_whitelisted',
    threshold: 0
  }, {
    dataKey: 'smooth.smooth_beaconing_score',
    threshold: 50
  }, {
    dataKey: 'new_domain.age_score',
    threshold: 50
  }, {
    dataKey: 'whois.age_score',
    threshold: 50
  }, {
    dataKey: 'whois.validity_score',
    threshold: 50
  }, {
    dataKey: 'domain.referer_score',
    threshold: 50
  }, {
    dataKey: 'domain.referer_ratio_score',
    threshold: 50
  }, {
    dataKey: 'domain.ua_ratio_score',
    threshold: 50
  }
];