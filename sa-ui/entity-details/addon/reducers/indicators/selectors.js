import { createSelector } from 'reselect';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import { entityType } from 'entity-details/reducers/entity/selectors';
import indicatorChartMap from 'entity-details/utils/indicator-chart-map';
import _ from 'lodash';

const _totalEvents = (state) => state.indicators.totalEvents;

const _historicalData = (state) => state.indicators.historicalData;

export const selectedIndicatorId = (state) => state.indicators.selectedIndicatorId;

export const eventFilter = (state) => state.indicators.eventFilter;

export const indicatorEvents = (state) => state.indicators.events;

export const indicatorGraphError = (state) => state.indicators.indicatorGraphError;

export const indicatorEventError = (state) => state.indicators.indicatorEventError;

export const historicalData = createSelector(
  [_historicalData],
  (historicalData) => {
    if (historicalData && historicalData.data) {
      return historicalData.data[0].data;
    }
  });

export const globalBaselineData = createSelector(
  [_historicalData],
  (historicalData) => {
    if (historicalData && historicalData.data && historicalData.data.length > 1) {
      return historicalData.data[1].data;
    }
  });

export const entityContexts = createSelector(
  [_historicalData],
  (historicalData) => {
    if (historicalData && historicalData.data) {
      const [{ contexts }] = historicalData.data;
      return _.transform(contexts, (result, value, key) => {
        result[key.camelize()] = value;
      });
    }
  });

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
      return (chartSettings || indicatorChartMap.timeAggregation).settings(anomalyTypeFieldName);
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

export const brokerId = createSelector(
  [indicatorEvents, entityType],
  (events, type) => {
    if (events) {
      const [event] = events;
      if (type === 'user' && event && event.user_link) {
        return event.user_link.match(/investigation\/(.*)\/events/i) ? event.user_link.match(/investigation\/(.*)\/events/i)[1] : '';
      }
    }
  });