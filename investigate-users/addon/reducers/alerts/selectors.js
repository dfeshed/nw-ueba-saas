import { createSelector } from 'reselect';
import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';

const _timeframesForDateTimeFilter = [
  {
    'name': 'LAST_SEVEN_DAYS',
    'unit': 'Days',
    'value': 7
  },
  {
    'name': 'LAST_TWO_WEEKS',
    'unit': 'Weeks',
    'value': 2
  },
  {
    'name': 'LAST_ONE_MONTH',
    'unit': 'Months',
    'value': 1
  },
  {
    'name': 'LAST_THREE_MONTH',
    'unit': 'Months',
    'value': 3
  },
  {
    'name': 'LAST_SIX_MONTH',
    'unit': 'Months',
    'value': 6
  }
];

const _alertList = (state) => state.alerts.alertList;

const _totalAlerts = (state) => state.alerts.totalAlerts;

const _alertsSeverity = (state) => state.alerts.alertsSeverity;

const _existAnomalyTypes = (state) => state.alerts.existAnomalyTypes;

const _alertsForTimeline = (state) => state.alerts.alertsForTimeline;

export const severityFilter = ['low', 'medium', 'high', 'critical'];

export const feedbackFilter = ['none', 'rejected'];

export const getTopAlerts = (state) => state.alerts.topAlerts;

export const getFilter = (state) => state.alerts.filter;

export const alertListError = (state) => state.alerts.alertListError;

export const topAlertsError = (state) => state.alerts.topAlertsError;

export const alertsForTimelineError = (state) => state.alerts.alertsForTimelineError;

export const getSelectedFeedBack = (state) => state.alerts.filter.feedback;

export const allAlertsReceived = createSelector(
  [_alertList, _totalAlerts],
  (alertList, totalAlerts) => {
    return totalAlerts && totalAlerts <= alertList.length;
  });


export const hasTopAlerts = createSelector(
  [getTopAlerts],
  (alerts) => {
    return alerts !== null && alerts.length > 0;
  });

export const getSelectedSeverity = createSelector(
  [getFilter],
  (filter) => {
    return filter.severity ? filter.severity.asMutable() : null;
  });

export const getAlertsSeverity = createSelector(
  [_alertsSeverity],
  (alertsSeverity) => {
    if (alertsSeverity) {
      return alertsSeverity.total_severity_count;
    }
  });

export const getAlertsGroupedHourly = createSelector(
  [_alertList],
  (alertList) => {
    return _.groupBy(alertList, 'startDate');
  });

export const hasAlerts = createSelector(
  [getAlertsGroupedHourly],
  (alerts) => {
    return _.keys(alerts).length > 0;
  });

export const getExistAnomalyTypes = createSelector(
  [_existAnomalyTypes],
  (existAnomalyTypes) => {
    const i18n = lookup('service:i18n');
    const anomalyTypesWithKey = _.mapValues(existAnomalyTypes, (value, key) => ({
      id: key,
      displayLabel: `${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${key}.name`)} (${value} Users)`
    }));
    return _.toArray(anomalyTypesWithKey);
  });

export const getSelectedAnomalyTypes = createSelector(
  [getFilter, getExistAnomalyTypes],
  (filter, anomalyTypes) => {
    return _.filter(anomalyTypes, ({ id }) => filter.indicator_types && filter.indicator_types.includes(id));
  });

export const dateTimeFilterOptionsForAlerts = createSelector(
  [getFilter],
  (filter) => {
    const filterOptions = {
      name: 'alertTimeRange',
      timeframes: _timeframesForDateTimeFilter,
      filterValue: {
        value: [ 3 ],
        unit: 'Months'
      }
    };
    if (filter.showCustomDate === true) {
      const [startDate, endDate] = filter.alert_start_range.split(',');
      filterOptions.filterValue = { value: [parseInt(startDate, 10), parseInt(endDate, 10)] };
    }
    return filterOptions;
  });

export const getAlertsForTimeline = createSelector(
  [_alertsForTimeline],
  (alertsForTimeline) => {
    if (!alertsForTimeline) {
      return null;
    }
    return alertsForTimeline.map((alert) => {
      const alertObj = { day: alert.day };
      let total = 0;
      alert.severities.forEach((severityMap) => {
        alertObj[severityMap.severity] = severityMap.count;
        total += severityMap.count;
      });
      alertObj.total = total;
      return alertObj;
    });
  });