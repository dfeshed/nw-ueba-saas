import reselect from 'reselect';

const { createSelector } = reselect;
const alertsState = (state) => state.respond.alerts;
const dictionaryState = (state) => state.respond.dictionaries;

/**
 * Produces the set of known alerts from state
 * @method getAlerts
 * @public
 */
export const getAlerts = createSelector(
  alertsState,
  (alertsState) => (alertsState.items)
);

/**
 * Produces the set of selected alerts from state
 * @method getSelectedAlerts
 * @public
 */
export const getSelectedAlerts = createSelector(
  alertsState,
  (alertsState) => (alertsState.itemsSelected)
);

/**
 * Determines whether or not any of the selected alerts are already part of an incident
 * @method hasSelectedAlertsBelongingToIncidents
 * @public
 * @returns {Boolean}
 */
export const hasSelectedAlertsBelongingToIncidents = createSelector(
  getAlerts,
  getSelectedAlerts,
  (alerts, selectedAlerts) => {
    return alerts.some((alert) => selectedAlerts.includes(alert.id) && alert.partOfIncident);
  }
);

/**
 * Produces a sorted array of alert names present in the system
 * @method getAlertNames
 * @public
 */
export const getAlertNames = createSelector(
  dictionaryState,
  (dictionaryState) => (dictionaryState.alertNames.asMutable().sort((a, b) => {
    return a.localeCompare(b);
  }))
);