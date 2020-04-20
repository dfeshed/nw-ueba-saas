export default {
  allTags: 'allTags',
  allReasons: 'allReasons',
  reputationCheckNullFields: ['customerPercentage', 'customerInvestigatedPercentage', 'customerHighRiskPercentage', 'customerRiskyFeedbackPercentage', 'customerSuspiciousFeedbackPercentage', 'customerNotRiskyFeedbackPercentage', 'customerUnknownFeedbackPercentage'],
  'LiveConnect-Ip': {
    info: 'IpInfo',
    Reputation: 'IpReputation',
    checkNullFields: ['asnNumber', 'asnOrganization', 'countryCode', 'country'],
    fetchRelatedEntities: ['lcRelatedFiles', 'lcRelatedDomains'],
    relatedEntityResponse: 'ips',
    relatedEntity: 'RelatedIps',
    relatedEntities_count: ['relatedFilesCount', 'relatedDomainsCount']
  },
  'LiveConnect-Domain': {
    info: 'DomainInfo',
    checkNullFields: ['ip', 'whoisRegType', 'whoisRegName', 'whoisRegOrg', 'whoisRegStreet', 'whoisRegCity', 'whoisRegState', 'whoisPostalCode', 'whoisCountry', 'whoisPhone', 'whoisFax', 'whoisEmail'],
    Reputation: 'DomainReputation',
    fetchRelatedEntities: ['lcRelatedIps', 'lcRelatedFiles'],
    relatedEntityResponse: 'domains',
    relatedEntity: 'RelatedDomains',
    relatedEntities_count: ['relatedIpsCount', 'relatedFilesCount']
  },
  'LiveConnect-File': {
    info: 'FileInfo', Reputation: 'FileReputation',
    checkNullFields: ['certIssuer', 'certSigAlgo', 'certSerial', 'certSubject', 'certThumbprint', 'fileName', 'md5', 'sha1', 'sha256', 'fileSize', 'mimeType'],
    fetchRelatedEntities: ['lcRelatedDomains', 'lcRelatedIps'],
    relatedEntityResponse: 'files',
    relatedEntity: 'RelatedFiles',
    relatedEntities_count: ['relatedIpsCount', 'relatedDomainsCount']
  }
};