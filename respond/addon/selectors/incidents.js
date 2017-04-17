import reselect from 'reselect';

const { createSelector } = reselect;

const incidentsState = (state) => state.respond.incidents;

const closedStatuses = ['CLOSED', 'CLOSED_FALSE_POSITIVE'];

export const hasSelectedClosedIncidents = createSelector(
  incidentsState,
  (incidentsState) => {
    const { incidentsSelected, incidents } = incidentsState;
    const selectedClosedIncidents = incidents.filter((item) => {
      return incidentsSelected.includes(item.id) && closedStatuses.includes(item.status);
    });
    return !!selectedClosedIncidents.length;
  }
);