import * as ACTION_TYPES from './types';
import { fetchData, exportData } from './fetch/data';
import { getFilter } from 'investigate-users/reducers/alerts/selectors';

const getTopTenAlerts = () => {
  return (dispatch) => {
    fetchData('alertOverview').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_TOP_ALERTS,
        payload: data
      });
    });
  };
};

const getAlertsForGivenTimeInterval = () => {
  return (dispatch, getState) => {
    const filter = getFilter(getState());
    fetchData('alertList', filter).then(({ data, info, total }) => {
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

const resetAlerts = () => ({ type: ACTION_TYPES.RESET_ALERTS });

export {
  getAlertsForGivenTimeInterval,
  getExistAnomalyTypesForAlert,
  updateFilter,
  getTopTenAlerts,
  resetAlerts,
  getAlertsForTimeline,
  exportAlerts
};