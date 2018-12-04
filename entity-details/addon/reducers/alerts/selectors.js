import { createSelector } from 'reselect';
import _ from 'lodash';

export const selectedAlertId = (state) => state.alerts.selectedAlertId;

export const alertsData = (state) => state.alerts.alerts;

export const getSelectedAlertData = createSelector(
  [selectedAlertId, alertsData],
  (id, alerts) => {
    if (id && alerts) {
      return _.find(alerts, { id });
    }
  });