/*
 * Represents the various sort options for incidents. The sortField property represents the db field used in the
 * service query to sort the result set
 */
const SORT_TYPES = [
  {
    id: 1,
    name: 'SCORE',
    sortField: 'riskScore',
    isDescending: true,
    label: 'Score',
    labelKey: 'respond.incidents.sort.score'
  },
  {
    id: 2,
    name: 'STATUS',
    sortField: 'statusSort',
    isDescending: true,
    label: 'Status',
    labelKey: 'respond.incidents.sort.status'
  },
  {
    id: 3,
    name: 'NEWEST',
    sortField: 'created',
    isDescending: true,
    label: 'Newest',
    labelKey: 'respond.incidents.sort.newest'
  },
  {
    id: 4,
    name: 'OLDEST',
    sortField: 'created',
    isDescending: false,
    label: 'Oldest',
    labelKey: 'respond.incidents.sort.oldest'
  }
];

const SORT_TYPES_BY_NAME = {};

SORT_TYPES.forEach((t) => SORT_TYPES_BY_NAME[t.name] = t);

export {
  SORT_TYPES,
  SORT_TYPES_BY_NAME
};