/*
 * Represents the various status options for incidents. This will be eventually replaced by data returned from the service
 */
const STATUS_TYPES = [
  {
    name: 'NEW',
    label: 'New',
    labelKey: 'respond.incidents.status.new'
  },
  {
    name: 'ASSIGNED',
    label: 'Assigned',
    labelKey: 'respond.incidents.status.assigned'
  },
  {
    name: 'IN_PROGRESS',
    label: 'In Progress',
    labelKey: 'respond.incidents.status.inProgress'
  },
  {
    name: 'REMEDIATION_REQUESTED',
    label: 'Remediation requested',
    labelKey: 'respond.incidents.status.remediationRequested'
  },
  {
    name: 'REMEDIATION_COMPLETE',
    label: 'Remediation completed',
    labelKey: 'respond.incidents.status.remediationComplete'
  },
  {
    name: 'CLOSED',
    label: 'Closed',
    labelKey: 'respond.incidents.status.closed'
  },
  {
    name: 'CLOSED_FALSE_POSITIVE',
    label: 'Closed (false positive)',
    labelKey: 'respond.incidents.status.closedFalsePositive'
  }
];

const STATUS_TYPES_BY_NAME = {};

STATUS_TYPES.forEach((t) => STATUS_TYPES_BY_NAME[t.name] = t);

export {
  STATUS_TYPES,
  STATUS_TYPES_BY_NAME
};