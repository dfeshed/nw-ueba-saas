import * as ACTION_TYPES from './types';
import { fetchData, exportData } from './fetch/data';
import { getFilter } from 'investigate-users/reducers/alerts/selectors';
import moment from 'moment';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';

const getTopTenAlerts = () => {
  return (dispatch) => {
    fetchData('alertOverview').then((result) => {
      if (result === 'error' || result.data.length === 0) {
        dispatch({
          type: ACTION_TYPES.TOP_ALERTS_ERROR,
          payload: result === 'error' ? 'topAlertsError' : 'noAlerts'
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
    const filter = getFilter(getState());
    fetchData('alertList', filter).then((result) => {
      const { data, info, total } = result;
      if (result === 'error' || (data && data.length === 0)) {
        dispatch({
          type: ACTION_TYPES.ALERT_LIST_ERROR,
          payload: result === 'error' ? 'alertListError' : 'noAlerts'
        });
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_ALERTS,
        payload: { data, info, total }
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
    }
    exportData('alertsExport', filter, `alerts_${new Date().toISOString()}.csv`).then(() => {});
  };
};

const updateFilter = (filter, needNotToPullAlerts) => {
  return (dispatch) => {
    dispatch(resetAlerts());
    if (filter) {
      filter = filter.setIn(['fromPage'], 1);
    }
    dispatch({
      type: ACTION_TYPES.UPDATE_FILTER_FOR_ALERTS,
      payload: filter
    });
    if (true !== needNotToPullAlerts) {
      dispatch(getAlertsForGivenTimeInterval(filter));
    }
  };
};

const updateDateRangeFilter = (filterOption) => {
  return (dispatch, getState) => {
    let filter = getFilter(getState());
    if (filterOption.operator === 'GREATER_THAN') {
      filter = filter.setIn(['alert_start_range'], `${moment().subtract(filterOption.unit, filterOption.value[0]).unix() * 1000}, ${moment().unix() * 1000}`);
    } else {
      if (!filterOption.value[0] || filterOption.value[0] > filterOption.value[1]) {
        return;
      }
      filter = filter.setIn(['alert_start_range'], filterOption.value.join(','));
    }
    dispatch(updateFilter(filter));
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