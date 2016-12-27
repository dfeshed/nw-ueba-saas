/**
 * @file Incident constants.
 * Any constant values related to the incident model object
 * @public
 */

/**
 * @type {{TODAY: string, LAST_HOUR: string, LAST_12_HOURS: string, LAST_24_HOURS: string, LAST_7_DAYS: string, ALL_DATA: string}}
 * @public
 */
export const dateTimeOptions = {
  TODAY: '0',
  LAST_HOUR: '1',
  LAST_12_HOURS: '2',
  LAST_24_HOURS: '3',
  LAST_7_DAYS: '4',
  ALL_DATA: '5',
  CUSTOM: '6'
};

export const incPriority = {
  CRITICAL: 3,
  HIGH: 2,
  MEDIUM: 1,
  LOW: 0
};

export const incStatus = {
  NEW: 0,
  ASSIGNED: 1,
  IN_PROGRESS: 2,
  REMEDIATION_REQUESTED: 3,
  REMEDIATION_COMPLETE: 4,
  CLOSED: 5,
  CLOSED_FALSE_POSITIVE: 6
};

export const incidentRiskThreshold = {
  LOW: 30,
  MEDIUM: 50,
  HIGH: 70
};

export const journalMilestones = {
  RECONNAISSANCE: 0,
  DELIVERY: 1,
  EXPLOITATION: 2,
  INSTALLATION: 3,
  COMMAND_AND_CONTROL: 4,
  ACTION_ON_OBJECTIVE: 5,
  CONTAINMENT: 6,
  ERADICATION: 7,
  CLOSURE: 8
};

// Note: Custom option is removed until rsa-form-datetime component's bug fixes are addressed.
export const dateTimeFilterOptions =
  Object.keys(dateTimeOptions).filter((k) => k !== 'CUSTOM').map((k) => dateTimeOptions[k]);

// returns : [0, 1, 2, 3]
export const incidentPriorityIds =
  Object.keys(incPriority).map((k) => incPriority[k]);

// returns : [0, 1, 2, 3, 4, 5, 6]
export const incidentStatusIds =
  Object.keys(incStatus).map((k) => incStatus[k]);

// returns {0: "NEW", 1: "ASSIGNED", 2: "IN_PROGRESS", 3: "REMEDIATION_REQUESTED", 4: "REMEDIATION_COMPLETE", 5: "CLOSED", 6: "FALSE_POSITIVE"}
export const incidentStatusString =
  Object.keys(incStatus).reduce(function(status, key) {
    status[ incStatus[key] ] = key;
    return status;
  }, {});

// returns {0: "LOW", 1: "MEDIUM", 2: "HIGH", 3: "CRITICAL"}
export const incidentPriorityString =
  Object.keys(incPriority).reduce(function(priority, key) {
    priority[ incPriority[key] ] = key;
    return priority;
  }, {});


export const C2Enrichment = {
  domainName: { key: 'rsa_analytics_http-${type}_c2_normalized_domain', isTitleScore: true },
  titleScore: { key: 'rsa_analytics_http-${type}_c2_command_control_aggregate', isTitleScore: true },
  beaconingScore: { key: 'rsa_analytics_http-${type}_c2_smooth_score' },
  newDomain: { key: 'rsa_analytics_http-${type}_c2_newdomain_score' },
  whoisAvailable: { key: 'rsa_analytics_http-${type}_c2_whois_domain_not_found_by_whois' },
  whoisAgeScore: { key: 'rsa_analytics_http-${type}_c2_whois_age_score', displayCondition: true },
  expiringDomain: { key: 'rsa_analytics_http-${type}_c2_whois_validity_score', displayCondition: true },
  rareDomain: { key: 'rsa_analytics_http-${type}_c2_referer_score' },
  referrer: { key: 'rsa_analytics_http-${type}_c2_referer_ratio_score' },
  userAgent: { key: 'rsa_analytics_http-${type}_c2_ua_ratio_score' }
};

export const WinAuthEnrichment = {
  eventComputer: { key: 'rsa_analytics_uba_winauth_normalized_hostname', isTitleScore: true },
  titleScore: { key: 'rsa_analytics_uba_winauth_aggregation_aggregate', isTitleScore: true },
  isDeviceExists: { key: 'rsa_analytics_uba_winauth_device_exists' },
  newDeviceScore: { key: 'rsa_analytics_uba_winauth_newdevicescore_score', displayCondition: true },
  highServerScore: { key: 'rsa_analytics_uba_winauth_highserverscore_score', displayCondition: false },
  newServerScore: { key: 'rsa_analytics_uba_winauth_newserverscore_score', displayCondition: false },
  passTheHash: { key: 'rsa_analytics_uba_winauth_newdeviceservice_score', displayCondition: false },
  failedServerScore: { key: 'rsa_analytics_uba_winauth_failedserversscore_score', displayCondition: false },
  logonType: { key: 'rsa_analytics_uba_winauth_logontypescore_score', displayCondition: false }
};
