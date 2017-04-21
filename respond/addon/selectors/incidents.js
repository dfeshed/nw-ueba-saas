import reselect from 'reselect';

const { createSelector } = reselect;

const incidentsState = (state) => state.respond.incidents;

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
    const { incidentsSelected, incidents } = incidentsState;
    return incidents.some((item) => incidentsSelected.includes(item.id) && closedStatuses.includes(item.status));
  }
);