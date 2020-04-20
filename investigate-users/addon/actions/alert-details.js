import * as ACTION_TYPES from './types';
import { fetchData, exportData } from './fetch/data';
import _ from 'lodash';
import { getFilter, alertsGroupedDaily, currentAlertsCount, topAlertsEntity, topAlertsTimeFrame } from 'investigate-users/reducers/alerts/selectors';
import moment from 'moment';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';
import { lookup } from 'ember-dependency-lookup';

// Severity order, alerts should be grouped in same order.
const _severityIndex = { Critical: 0, High: 1, Medium: 2, Low: 3 };

/**
 * This function will iterate over alertList fetched from server and will grouped on daily basis.
 * If this is first alert for given day. Then new object entry will be created with that day as key. Value is always four array.
 * First array will have critical alerts, second for High, third for Medium and last one for Low. Alert will be added at end of respective severity array.
 *
 * Ex: Intial alertList = {};
 *  After first alert --> { 'Jul12 2019' : [[], [], [], []]};
 * { 'Jul12 2019' : [[], [], [{First Medium Alert}], []]};
 *
 * After alert of different severity-->
 * { 'Jul12 2019' : [[], [{ First High Alert}], [{First Medium Alert}], []]};
 *
 * After second alert of same severity-->
 * { 'Jul12 2019' : [[], [{ First High Alert}], [{First Medium Alert}, {Second Medium Alert}], []]};
 *
 * After alert from different day-->
 * { 'Jul12 2019' : [[], [{ First High Alert}], [{First Medium Alert}, {Second Medium Alert}], []]},
 * { 'Jul13 2019' : [[], [{ First High Alert}], [], []]};
 * @private
 *  */
const _getAlertsGroupedDaily = (currentGroupedAlerts, alertsList) => {
  _.forEach(alertsList, (alert) => {
    const timezone = lookup('service:timezone');
    const i18n = lookup('service:i18n');
    const alertDay = moment(alert.startDate)
      .locale(i18n.primaryLocale || 'en')
      .tz(timezone.selected ? timezone.selected.zoneId : 'UTC')
      .format('MMM DD YYYY');
    const alertsForDay = currentGroupedAlerts[alertDay];
    const severityIndex = _severityIndex[alert.severity];
    if (alertsForDay) {
      // Using custom sorting instead of java script function to avoid multiple loops and improving performance.
      currentGroupedAlerts[alertDay] = currentGroupedAlerts[alertDay].asMutable ? currentGroupedAlerts[alertDay].asMutable() : currentGroupedAlerts[alertDay];
      currentGroupedAlerts[alertDay][severityIndex] = currentGroupedAlerts[alertDay].get(severityIndex).concat([alert]);
    } else {
      currentGroupedAlerts[alertDay] = [[], [], [], []];
      currentGroupedAlerts[alertDay][severityIndex] = [alert];
    }
  });
  return currentGroupedAlerts;
};

const _buildTimeRange = (timeRange) => {
  return `${moment().subtract(timeRange.unit.toLowerCase(), timeRange.value).unix() * 1000},${moment().unix() * 1000}`;
};

const getTopTenAlerts = (entityType, timeRange) => {
  return (dispatch, getState) => {
    entityType = entityType || topAlertsEntity(getState());
    timeRange = timeRange || topAlertsTimeFrame(getState());
    const filter = { alert_start_range: _buildTimeRange(timeRange) };
    if (entityType !== 'all') {
      filter.entityType = entityType;
    }
    dispatch({
      type: ACTION_TYPES.TOP_ALERT_FILTER,
      payload: { entityType, timeRange }
    });
    fetchData('alertOverview', filter).then((result) => {
      if (result === 'error' || result.data.length === 0) {
        dispatch({
          type: ACTION_TYPES.TOP_ALERTS_ERROR,
          payload: result === 'error' ? 'topAlertsError' : 'noAlerts'
        });
        dispatch({
          type: ACTION_TYPES.GET_TOP_ALERTS,
          payload: []
        });
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_TOP_ALERTS,
        payload: result.data
      });
    });
  };
};

const getAlertsForGivenTimeInterval = () => {
  return (dispatch, getState) => {
    let filter = getFilter(getState());
    if (filter.entityType === 'all') {
      filter = _.omit(filter, 'entityType');
    }

    fetchData('alertList', filter).then((result) => {
      const { data, info, total } = result;
      if (result === 'error' || !data || data.length === 0) {
        dispatch({
          type: ACTION_TYPES.ALERT_LIST_ERROR,
          payload: result === 'error' ? 'alertListError' : 'noAlerts'
        });
        return;
      }
      const currentGroupedAlerts = alertsGroupedDaily(getState());
      const currentCount = currentAlertsCount(getState()) + data.length;
      const _groupedAlerts = _getAlertsGroupedDaily(currentGroupedAlerts, data);
      dispatch({
        type: ACTION_TYPES.GET_ALERTS,
        payload: { data: _groupedAlerts, info, total, currentCount }
      });
    });
  };
};

const getExistAnomalyTypesForAlert = () => {
  return (dispatch) => {
    fetchData('existAnomalyTypesForAlerts').then((data) => {
      if (data === 'error') {
        flashErrorMessage('investigateUsers.errorMessages.unableToGetExistAnomalyTypesForAlert');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_EXIST_ANOMALY_TYPES_ALERT,
        payload: data
      });
    });
  };
};

const getAlertsForTimeline = () => {
  return (dispatch) => {
    fetchData('alertTimeline').then((data) => {
      if (data === 'error' || data.length === 0) {
        dispatch({
          type: ACTION_TYPES.ALERTS_FOR_TIMELINE_ERROR,
          payload: data === 'error' ? 'alertsForTimeLineError' : 'noAlerts'
        });
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_ALERTS_FOR_TIMELINE,
        payload: data
      });
    });
  };
};

const exportAlerts = () => {
  return (dispatch, getState) => {
    let filter = getFilter(getState());
    if (filter) {
      filter = filter.setIn(['fromPage'], 1);
      if (filter.entityType === 'all') {
        filter = _.omit(filter, 'entityType');
      }
    }
    exportData('alertsExport', filter, `alerts_${new Date().toISOString()}.csv`).then(() => {});
  };
};

const updateFilter = (filter, needNotToPullAlerts, relativeDateFilter) => {
  return (dispatch) => {
    dispatch(resetAlerts());
    if (filter) {
      filter = filter.setIn(['fromPage'], 1);
    }
    dispatch({
      type: ACTION_TYPES.UPDATE_FILTER_FOR_ALERTS,
      payload: { filter, relativeDateFilter }
    });
    if (true !== needNotToPullAlerts) {
      dispatch(getAlertsForGivenTimeInterval(filter));
    }
  };
};

const updateDateRangeFilter = (filterOption) => {
  return (dispatch, getState) => {
    let filter = getFilter(getState());
    if (filterOption.operator === 'LESS_THAN') {
      filter = filter.setIn(['showCustomDate'], false);
      filter = filter.setIn(['alert_start_range'], `${moment().subtract(filterOption.unit.toLowerCase(), filterOption.value[0]).unix() * 1000},${moment().unix() * 1000}`);
    } else if (filterOption.operator === 'GREATER_THAN') {
      filter = filter.setIn(['alert_start_range'], `${moment().subtract(filterOption.unit, filterOption.value[0]).unix() * 1000}, ${moment().unix() * 1000}`);
    } else {
      if (!filterOption.value[0] || filterOption.value[0] > filterOption.value[1]) {
        return;
      }
      filter = filter.setIn(['showCustomDate'], true);
      filter = filter.setIn(['alert_start_range'], filterOption.value.join(','));
    }
    dispatch(updateFilter(filter, false, filterOption.operator === 'LESS_THAN' ? filterOption : null));
  };
};

const resetAlerts = () => ({ type: ACTION_TYPES.RESET_ALERTS });

export {
  getAlertsForGivenTimeInterval,
  getExistAnomalyTypesForAlert,
  updateFilter,
  getTopTenAlerts,
  resetAlerts,
  getAlertsForTimeline,
  exportAlerts,
  updateDateRangeFilter
};