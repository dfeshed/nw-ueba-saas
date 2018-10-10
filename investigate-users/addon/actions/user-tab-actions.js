import * as ACTION_TYPES from './types';
import { fetchData } from './fetch/data';
import { getUserFilter } from 'investigate-users/reducers/users/selectors';

const getSeverityDetailsForUserTabs = (filter) => {
  return (dispatch) => {
    fetchData('severityBarForUser', filter).then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_SEVERITY_FOR_USERS,
        payload: data
      });
    });
  };
};
const getExistAnomalyTypes = () => {
  return (dispatch) => {
    fetchData('existAnomalyTypes').then((data) => {
      dispatch({
        type: ACTION_TYPES.GET_EXIST_ANOMALY_TYPES,
        payload: data
      });
    });
  };
};

const getExistAlertTypess = () => {
  return (dispatch) => {
    fetchData('existAlertTypes').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_EXIST_ALERT_TYPES,
        payload: data
      });
    });
  };
};

const getFavorites = () => {
  return (dispatch) => {
    fetchData('favoriteFilter').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_FAVORITES,
        payload: data
      });
    });
  };
};

const getUsers = (filter) => {
  return (dispatch, getState) => {
    filter = filter || getUserFilter(getState());
    fetchData('userList', filter).then(({ data, total }) => {
      dispatch({
        type: ACTION_TYPES.GET_USERS,
        payload: { data, total }
      });
    });
  };
};

const updateFilter = (filter) => {
  return (dispatch, getState) => {
    filter = filter || getUserFilter(getState());
    dispatch(resetUsers());
    if (filter) {
      filter = filter.setIn(['fromPage'], 1);
    }
    dispatch({
      type: ACTION_TYPES.UPDATE_FILTER_FOR_USERS,
      payload: filter
    });
    dispatch(getSeverityDetailsForUserTabs(filter));
    dispatch(getUsers(filter));
  };
};

const resetUsers = () => ({ type: ACTION_TYPES.RESET_USERS });

export {
  getSeverityDetailsForUserTabs,
  getExistAnomalyTypes,
  getExistAlertTypess,
  getFavorites,
  resetUsers,
  updateFilter,
  getUsers
};