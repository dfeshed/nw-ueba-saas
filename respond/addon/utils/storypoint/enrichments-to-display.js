/**
 * Ordered list of possible enrichments to be displayed (if given) for each storypoint.
 * @public
 */
export default [
  // C2 packet model enrichments:
  {
    dataKey: 'http-packet.c2.contexthub_whitelist_enrich.domain_is_whitelisted',
    threshold: 0
  }, {
    dataKey: 'http-packet.c2.smooth.score',
    threshold: 50
  }, {
    dataKey: 'http-packet.c2.newdomain.score',
    threshold: 50
  }, {
    dataKey: 'http-packet.c2.whois.age_scoreNetWitness',
    threshold: 50
  }, {
    dataKey: 'http-packet.c2.whois.validity_scoreNetWitness',
    threshold: 50
  }, {
    dataKey: 'http-packet.c2.referer.score',
    threshold: 50
  }, {
    dataKey: 'http-packet.c2.referer.ratio_score',
    threshold: 50
  }, {
    dataKey: 'http-packet.c2.ua.ratio_score',
    threshold: 50
  },

  // C2 log model enrichments:
  {
    dataKey: 'http-log.c2.contexthub_whitelist_enrich.domain_is_whitelisted',
    threshold: 0
  }, {
    dataKey: 'http-log.c2.smooth.score',
    threshold: 50
  }, {
    dataKey: 'http-log.c2.newdomain.score',
    threshold: 50
  }, {
    dataKey: 'http-log.c2.whois.age_scoreNetWitness',
    threshold: 50
  }, {
    dataKey: 'http-log.c2.whois.validity_scoreNetWitness',
    threshold: 50
  }, {
    dataKey: 'http-log.c2.referer.score',
    threshold: 50
  }, {
    dataKey: 'http-log.c2.referer.ratio_score',
    threshold: 50
  }, {
    dataKey: 'http-log.c2.ua.ratio_score',
    threshold: 50
  }
];