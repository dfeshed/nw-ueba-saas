import reselect from 'reselect';

const { createSelector } = reselect;

const incidentsState = (state) => state.respond.incidents;
const incidentState = (state) => state.respond.incident;

const closedStatuses = ['CLOSED', 'CLOSED_FALSE_POSITIVE'];

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
    return items.some((item) => itemsSelected.includes(item.id) && closedStatuses.includes(item.status));
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