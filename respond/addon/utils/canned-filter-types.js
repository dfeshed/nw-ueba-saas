/*
 * Represents the various canned incidents filters. The filter property represents the field and value used in the
 * service query to filter the result set
 */
const CANNED_FILTER_TYPES = [
  {
    id: 1,
    name: 'UNASSIGNED',
    label: 'Unassigned',
    labelKey: 'respond.incidents.precannedFilters.unassigned',
    filter: {
      field: 'status',
      value: 'NEW'
    }
  },
  {
    id: 2,
    name: 'ASSIGNED_TO_ME',
    label: 'Assigned to me',
    labelKey: 'respond.incidents.precannedFilters.assignedToMe',
    disabled: true,
    filter: [
      {
        field: 'status',
        value: 'ASSIGNED'
      },
      {
        field: 'assignee.id',
        value: undefined
      }
    ]
  },
  {
    id: 3,
    name: 'IN_PROGRESS',
    label: 'In Progress',
    labelKey: 'respond.incidents.precannedFilters.inProgress',
    filter: {
      field: 'status',
      value: 'IN_PROGRESS'
    }
  },
  {
    id: 4,
    name: 'ALL',
    label: 'Unassigned',
    labelKey: 'respond.incidents.precannedFilters.all',
    filter: []
  }
];

const CANNED_FILTER_TYPES_BY_NAME = {};
CANNED_FILTER_TYPES.forEach((t) => CANNED_FILTER_TYPES_BY_NAME[t.name] = t);

export {
  CANNED_FILTER_TYPES,
  CANNED_FILTER_TYPES_BY_NAME
};