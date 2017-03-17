/*
 * Represents the various sort options for incidents. The sortField property represents the db field used in the
 * service query to sort the result set
 */
const SORT_TYPES = [
  {
    name: 'ALERT_COUNT_DESC',
    sortField: 'alertCount',
    isDescending: true,
    label: 'Alerts (descending)',
    labelKey: 'respond.incidents.sort.alertsCountDescending'
  },
  {
    name: 'ALERT_COUNT_ASC',
    sortField: 'alertCount',
    isDescending: false,
    label: 'Alerts (ascending)',
    labelKey: 'respond.incidents.sort.alertsCountAscending'
  },
  {
    name: 'ASSIGNEE_ASC',
    sortField: 'assignee.name',
    isDescending: false,
    label: 'Assignee (ascending)',
    labelKey: 'respond.incidents.sort.assigneeAscending'
  },
  {
    name: 'ASSIGNEE_DESC',
    sortField: 'assignee.name',
    isDescending: true,
    label: 'Assignee (descending)',
    labelKey: 'respond.incidents.sort.assigneeDescending'
  },
  {
    name: 'CREATED_DESC',
    sortField: 'created',
    isDescending: true,
    label: 'Created (descending)',
    labelKey: 'respond.incidents.sort.createdDescending'
  },
  {
    name: 'CREATED_ASC',
    sortField: 'created',
    isDescending: false,
    label: 'Created (ascending)',
    labelKey: 'respond.incidents.sort.createdAscending'
  },
  {
    name: 'ID_ASC',
    sortField: 'created',
    isDescending: false,
    label: 'Created (ascending)',
    labelKey: 'respond.incidents.sort.idAscending'
  },
  {
    name: 'ID_DESC',
    sortField: 'created',
    isDescending: true,
    label: 'ID (descending)',
    labelKey: 'respond.incidents.sort.idDescending'
  },
  {
    name: 'NAME_ASC',
    sortField: 'name',
    isDescending: false,
    label: 'Name (ascending)',
    labelKey: 'respond.incidents.sort.nameAscending'
  },
  {
    name: 'NAME_DESC',
    sortField: 'name',
    isDescending: true,
    label: 'Name (descending)',
    labelKey: 'respond.incidents.sort.nameDescending'
  },
  {
    name: 'PRIORITY_DESC',
    sortField: 'priority',
    isDescending: true,
    label: 'Priority (descending)',
    labelKey: 'respond.incidents.sort.priorityDescending'
  },
  {
    name: 'PRIORITY_ASC',
    sortField: 'priority',
    isDescending: false,
    label: 'Priority (ascending)',
    labelKey: 'respond.incidents.sort.priorityAscending'
  },
  {
    name: 'SCORE_DESC',
    sortField: 'riskScore',
    isDescending: true,
    label: 'Score (descending)',
    labelKey: 'respond.incidents.sort.scoreDescending'
  },
  {
    name: 'SCORE_ASC',
    sortField: 'riskScore',
    isDescending: false,
    label: 'Score (ascending)',
    labelKey: 'respond.incidents.sort.scoreAscending'
  },
  {
    name: 'STATUS_ASC',
    sortField: 'status',
    isDescending: false,
    label: 'Status (ascending)',
    labelKey: 'respond.incidents.sort.statusAscending'
  },
  {
    name: 'STATUS_DESC',
    sortField: 'status',
    isDescending: true,
    label: 'Status (descending)',
    labelKey: 'respond.incidents.sort.statusDescending'
  }
];

const SORT_TYPES_BY_NAME = {};

SORT_TYPES.forEach((t) => SORT_TYPES_BY_NAME[t.name] = t);

export {
  SORT_TYPES,
  SORT_TYPES_BY_NAME
};