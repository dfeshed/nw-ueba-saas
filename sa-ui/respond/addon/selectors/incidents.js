import reselect from 'reselect';
import { isIncidentClosed } from 'respond/helpers/is-incident-closed';

const { createSelector } = reselect;

const incidentsState = (state) => state.respond.incidents;
const incidentState = (state) => state.respond.incident;

/**
 * Because some operations on incidents (e.g., changing priority or assignee) are not available if the status of an
 * incident is closed, this method will determine whether or not any of the currently selected incidents has a
 * closed status.
 * @method hasSelectedClosedIncidents
 * @public
 * @returns {Boolean}
 */
export const hasSelectedClosedIncidents = createSelector(
  incidentsState,
  (incidentsState) => {
    const { itemsSelected, items } = incidentsState;
    return items.some((item) => itemsSelected.includes(item.id) && isIncidentClosed(item.status));
  }
);

export const getIncidentId = createSelector(
  incidentState,
  (incidentState) => incidentState.id
);

export const getIncidentInfo = createSelector(
  incidentState,
  (incidentState) => incidentState.info
);

export const getIncidentInfoStatus = createSelector(
  incidentState,
  (incidentState) => incidentState.infoStatus
);

export const getViewMode = createSelector(
  incidentState,
  (incidentState) => incidentState.viewMode
);

export const getInspectorWidth = createSelector(
  incidentState,
  (incidentState) => incidentState.inspectorWidth
);

export const getTasksStatus = createSelector(
  incidentState,
  (incidentState) => incidentState.tasksStatus
);

export const getVisibleEntityTypes = createSelector(
  incidentState,
  (incidentState) => incidentState.visibleEntityTypes
);

export const getItemsFilters = createSelector(
  incidentsState,
  (incidentsState) => incidentsState.itemsFilters || {}
);

export const getPriorityFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters.priority || []
);

export const getStatusFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters.status || []
);

export const getIdFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters.id
);

export const getIsUnassignedFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters.assignee && itemsFilters.assignee.isNull
);

export const getAssigneeFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters['assignee.id']
);

export const hasAssigneeFilter = createSelector(
  getAssigneeFilters,
  (assigneeFilters) => (assigneeFilters || []).length > 0
);

export const getCategoryFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters['categories.parent']
);

export const getSentToArcherFilters = createSelector(
  getItemsFilters,
  (itemsFilters) => itemsFilters.sentToArcher || []
);

export const isSendToArcherAvailable = createSelector(
  incidentsState,
  (incidentsState) => incidentsState.isSendToArcherAvailable
);