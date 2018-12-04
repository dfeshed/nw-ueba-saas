import { createSelector } from 'reselect';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import _ from 'lodash';

export const indicatorId = (state) => state.indicators.indicatorId;

export const eventFilter = (state) => state.indicators.eventFilter;

export const getIncidentData = createSelector(
  [indicatorId, getSelectedAlertData],
  (id, alertData) => {
    if (id && alertData) {
      return _.find(alertData.evidences, { id });
    }
  });

export const getIncidentKey = createSelector(
  [getIncidentData],
  (incidentData) => {
    if (incidentData) {
      return incidentData.anomalyTypeFieldName;
    }
  });