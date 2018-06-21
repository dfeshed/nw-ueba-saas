import reselect from 'reselect';

const { createSelector } = reselect;
const _context = (state) =>
  (state.endpoint && state.endpoint.visuals.lookupData) || (state.files && state.files.fileList.lookupData);

export const getAlertsCount = createSelector(
  [_context],
  (context) => {
    let count = 0;
    if (context && context[0].Alerts) {
      count = context[0].Alerts.resultList.length;
    }
    return count;
  }
);

export const getIncidentsCount = createSelector(
  [_context],
  (context) => {
    let count = 0;
    if (context && context[0].Incidents) {
      count = context[0].Incidents.resultList.length;
    }
    return count;
  }
);
