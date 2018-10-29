import * as ACTION_TYPES from './types';
import { fetchData, exportData } from './fetch/data';
import { getUserFilter } from 'investigate-users/reducers/users/selectors';
import _ from 'lodash';

const _filterPropertiesToPickFromCurrentFilterForPosting = ['alertTypes', 'indicatorTypes', 'isWatched', 'minScore', 'severity', 'sortDirection', 'sortField'];

const _removeUnwantedPropertyFromObject = (obj = {}) => {
  return _.pick(obj, _filterPropertiesToPickFromCurrentFilterForPosting);
};

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

const updateFilter = (filter, needNotToPullUsers) => {
  return (dispatch, getState) => {
    filter = (filter === 'RESET') ? null : filter || getUserFilter(getState());
    dispatch(resetUsers());
    if (filter) {
      filter = filter.setIn(['fromPage'], 1);
    }
    dispatch({
      type: ACTION_TYPES.UPDATE_FILTER_FOR_USERS,
      payload: filter
    });
    if (true !== needNotToPullUsers) {
      dispatch(getSeverityDetailsForUserTabs(filter));
      dispatch(getUsers(filter));
    }
  };
};

const saveAsFavorite = (name) => {
  return (dispatch, getState) => {
    const filterForPost = _removeUnwantedPropertyFromObject(getUserFilter(getState()));
    fetchData('createfavoriteFilter', filterForPost, 'POST', name).then(() => {
      dispatch(getFavorites());
    });
  };
};

const deleteFavorite = (filterId) => {
  return (dispatch) => {
    fetchData('deletefavoriteFilter', null, 'DELETE', filterId).then(() => {
      dispatch(getFavorites());
    });
  };
};

const exportUsers = () => {
  return (dispatch, getState) => {
    let filter = getUserFilter(getState());
    if (filter) {
      filter = filter.setIn(['fromPage'], 1);
    }
    exportData('usersExport', filter, false, null, true, `users_${new Date().toISOString()}.csv`).then(() => {});
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
  getUsers,
  saveAsFavorite,
  exportUsers,
  deleteFavorite
};