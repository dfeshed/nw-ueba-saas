/*
 * Represents the various priority options for incidents. This will be eventually replaced by data returned from the service
 */
const PRIORITY_TYPES = [
  {
    name: 'CRITICAL',
    label: 'Critical',
    labelKey: 'respond.incidents.priorities.critical'
  },
  {
    name: 'HIGH',
    label: 'High',
    labelKey: 'respond.incidents.priorities.high'
  },
  {
    name: 'MEDIUM',
    label: 'Medium',
    labelKey: 'respond.incidents.priorities.medium'
  },
  {
    name: 'LOW',
    label: 'Low',
    labelKey: 'respond.incidents.priorities.low'
  }
];

const PRIORITY_TYPES_BY_NAME = {};

PRIORITY_TYPES.forEach((t) => PRIORITY_TYPES_BY_NAME[t.name] = t);

export {
  PRIORITY_TYPES,
  PRIORITY_TYPES_BY_NAME
};