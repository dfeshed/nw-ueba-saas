import reselect from 'reselect';

const { createSelector } = reselect;
const alertsState = (state) => state.respond.alerts;

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