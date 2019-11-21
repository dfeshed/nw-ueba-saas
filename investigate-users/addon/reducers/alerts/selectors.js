import { createSelector } from 'reselect';
import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';
import entityAnomalyMap from 'investigate-users/utils/entity-anomaly-map';
import moment from 'moment';

const _totalAlerts = (state) => state.alerts.totalAlerts;

const _alertsSeverity = (state) => state.alerts.alertsSeverity;

const _existAnomalyTypes = (state) => state.alerts.existAnomalyTypes;

const _alertsForTimeline = (state) => state.alerts.alertsForTimeline;

const _relativeDateFilter = (state) => state.alerts.relativeDateFilter;

const _findEntityTypeFromAnomalyKey = (entityType, key) => {
  if (entityType !== 'all') {
    return entityType;
  } else {
    for (const entity in entityAnomalyMap) {
      if (entityAnomalyMap[entity].includes(key)) {
        return entity;
      }
    }
  }
};

export const timeframesForDateTimeFilter = [
  {
    'name': 'IN_LAST_TWENTY_FOUR_HOURS',
    'unit': 'Hours',
    'value': 24
  },
  {
    'name': 'IN_LAST_SEVEN_DAYS',
    'unit': 'Days',
    'value': 7
  },
  {
    'name': 'IN_LAST_ONE_MONTH',
    'unit': 'Months',
    'value': 1
  },
  {
    'name': 'IN_LAST_THREE_MONTH',
    'unit': 'Months',
    'value': 3
  }
];

export const severityFilter = ['low', 'medium', 'high', 'critical'];

export const entityFilter = ['all', 'userId', 'ja3', 'sslSubject'];

export const feedbackFilter = ['none', 'rejected'];

export const getTopAlerts = (state) => state.alerts.topAlerts;

export const topAlertsEntity = (state) => state.alerts.topAlertsEntity;

export const topAlertsTimeFrame = (state) => state.alerts.topAlertsTimeFrame;

export const getFilter = (state) => state.alerts.filter;

export const alertListError = (state) => state.alerts.alertListError;

export const topAlertsError = (state) => state.alerts.topAlertsError;

export const alertsForTimelineError = (state) => state.alerts.alertsForTimelineError;

export const getSelectedFeedBack = (state) => state.alerts.filter.feedback;

export const currentAlertsCount = (state) => state.alerts.currentAlertsCount;

export const alertsGroupedDaily = (state) => state.alerts.alertList.asMutable();

export const allAlertsReceived = createSelector(
  [currentAlertsCount, _totalAlerts],
  (currentCount, totalAlerts) => {
    return totalAlerts === currentCount;
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

export const selectedEntities = createSelector(
  [getFilter],
  (filter) => {
    if (filter.entityType) {
      return filter.entityType;
    }
  });

export const getAlertsSeverity = createSelector(
  [_alertsSeverity],
  (alertsSeverity) => {
    if (alertsSeverity) {
      const { Critical, High, Medium, Low } = alertsSeverity.total_severity_count;
      return { Critical, High, Medium, Low };
    }
  });

export const hasAlerts = createSelector(
  [alertsGroupedDaily],
  (alerts) => {
    return _.keys(alerts).length > 0;
  });

export const getExistAnomalyTypes = createSelector(
  [_existAnomalyTypes, selectedEntities],
  (existAnomalyTypes, entityType) => {
    const anomalyKeys = entityAnomalyMap[entityType];
    const i18n = lookup('service:i18n');
    const mappedArray = [];
    _.forEach(existAnomalyTypes, (value, key) => {
      const entityTypeLabel = _findEntityTypeFromAnomalyKey(entityType, key);
      const displayLabel = `${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${key}.name`)} (${value} ${i18n.t(`investigateUsers.entityTypes.${entityTypeLabel}`)})`;
      if (entityType === 'all' || anomalyKeys.includes(key)) {
        mappedArray.push({
          id: key,
          displayLabel
        });
      }
    });
    return mappedArray;
  });

export const getSelectedAnomalyTypes = createSelector(
  [getFilter, getExistAnomalyTypes],
  (filter, anomalyTypes) => {
    return _.filter(anomalyTypes, ({ id }) => filter.indicator_types && filter.indicator_types.includes(id));
  });

export const dateTimeFilterOptionsForAlerts = createSelector(
  [getFilter, _relativeDateFilter],
  (filter, relativeDateFilter) => {
    const filterOptions = {
      name: 'alertTimeRange',
      timeframes: timeframesForDateTimeFilter,
      filterValue: relativeDateFilter
    };
    if (filter.showCustomDate === true) {
      const [startDate, endDate] = filter.alert_start_range.split(',');
      filterOptions.filterValue = { value: [parseInt(startDate, 10), parseInt(endDate, 10)] };
      filterOptions.includeTimezone = true;
    }
    return filterOptions;
  });

export const getAlertsForTimeline = createSelector(
  [_alertsForTimeline],
  (alertsForTimeline) => {
    if (!alertsForTimeline) {
      return null;
    }
    const i18n = lookup('service:i18n');
    const timezone = lookup('service:timezone');
    return alertsForTimeline.map((alert) => {
      const alertDay = moment(alert.day)
        .locale(i18n.locale || 'en')
        .tz(timezone.selected ? timezone.selected.zoneId : 'UTC')
        .format('DD-MMM');
      const alertObj = { day: alertDay, originalTime: alert.day };
      let total = 0;
      alert.severities.forEach((severityMap) => {
        alertObj[severityMap.severity] = severityMap.count;
        total += severityMap.count;
      });
      alertObj.total = total;
      return alertObj;
    });
  });