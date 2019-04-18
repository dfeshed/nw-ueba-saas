import { createSelector } from 'reselect';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import indicatorChartMap from 'entity-details/utils/indicator-chart-map';
import _ from 'lodash';

const _totalEvents = (state) => state.indicators.totalEvents;

export const selectedIndicatorId = (state) => state.indicators.selectedIndicatorId;

export const eventFilter = (state) => state.indicators.eventFilter;

export const indicatorEvents = (state) => state.indicators.events;

export const historicalData = (state) => state.indicators.historicalData;

export const areAllEventsReceived = createSelector(
  [_totalEvents, indicatorEvents],
  (total, events) => {
    if (total && events) {
      return total === events.length;
    }
  });

export const getIncidentData = createSelector(
  [selectedIndicatorId, getSelectedAlertData],
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

/**
 * This selector returns chart settings based on anomalyt type.
 * @private
 */
export const indicatorMapSettings = createSelector(
  [getIncidentKey],
  (anomalyTypeFieldName) => {
    if (anomalyTypeFieldName) {
      const chartSettings = _.find(indicatorChartMap, (chartType) => chartType.anomalyTypeFieldName.includes(anomalyTypeFieldName));
      return (chartSettings || indicatorChartMap.activityTimeAnomalySettings).settings(anomalyTypeFieldName);
    }
  });

export const getIndicatorEntity = createSelector(
  [getIncidentData],
  (incidentData) => {
    if (incidentData) {
      return incidentData.dataEntitiesIds[0];
    }
  });

const _getIncidentIdArray = createSelector(
  [getSelectedAlertData],
  (alertData) => {
    if (alertData) {
      return _.map(alertData.evidences, 'id');
    }
  });

export const getIncidentPositionAndNextIncidentId = createSelector(
  [selectedIndicatorId, _getIncidentIdArray],
  (indicatorId, incidentIdArray) => {
    if (incidentIdArray) {
      const indicatorLength = incidentIdArray.length;
      if (indicatorId) {
        const incidentIndex = _.indexOf(incidentIdArray, indicatorId);
        return {
          previousIndicatorId: incidentIndex === 0 ? null : incidentIdArray[incidentIndex - 1],
          currentPosition: incidentIndex + 1,
          nextIndicatorId: indicatorLength - 1 === incidentIndex ? null : incidentIdArray[incidentIndex + 1],
          indicatorLength
        };
      } else {
        return { previousIndicatorId: null, currentPosition: 0, nextIndicatorId: incidentIdArray[0], indicatorLength };
      }
    }
  });