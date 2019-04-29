import * as ACTION_TYPES from './types';
import { fetchData, exportData } from './fetch/data';
import { getUserFilter } from 'investigate-users/reducers/users/selectors';
import { getWatchedUserCount } from './user-details';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';
import _ from 'lodash';

const _filterPropertiesToPickFromCurrentFilterForPosting = ['alertTypes', 'indicatorTypes', 'isWatched', 'minScore', 'severity', 'sortDirection', 'sortField'];

const _removeUnwantedPropertyFromObject = (obj = {}) => {
  return _.pick(obj, _filterPropertiesToPickFromCurrentFilterForPosting);
};

const _followUnfollowUsers = (endpointLocation) => {
  return (dispatch, getState) => {
    let filter = getUserFilter(getState());
    const filterForPost = _removeUnwantedPropertyFromObject(filter);
    fetchData(endpointLocation, filterForPost, 'POST').then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.users.unableToFollowUsers');
        return;
      }
      dispatch(resetUsers());
      if (filter) {
        filter = filter.setIn(['fromPage'], 1);
      }
      dispatch(getUsers(filter));
      dispatch(getSeverityDetailsForUserTabs(filter));
      dispatch(getWatchedUserCount());
    });
  };
};

const getSeverityDetailsForUserTabs = (filter) => {
  return (dispatch) => {
    fetchData('severityBarForUser', filter).then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.users.unableToGetSeverityDetails');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_SEVERITY_FOR_USERS,
        payload: result.data
      });
    });
  };
};
const getExistAnomalyTypes = () => {
  return (dispatch) => {
    fetchData('existAnomalyTypes').then((data) => {
      if (data === 'error') {
        flashErrorMessage('investigateUsers.users.unableToGETExistAnomalyTypes');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_EXIST_ANOMALY_TYPES,
        payload: data
      });
    });
  };
};

const getExistAlertTypess = () => {
  return (dispatch) => {
    fetchData('existAlertTypes').then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.users.unableToGetExistAlertTypes');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_EXIST_ALERT_TYPES,
        payload: result.data
      });
    });
  };
};

const getFavorites = () => {
  return (dispatch) => {
    fetchData('favoriteFilter').then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.users.unableToGetExistAlertTypes');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_FAVORITES,
        payload: result.data
      });
    });
  };
};

const getUsers = (filter) => {
  return (dispatch, getState) => {
    filter = filter || getUserFilter(getState());
    fetchData('userList', filter).then((result) => {
      const { data, total, info } = result;
      if (result === 'error' || (data && data.length === 0)) {
        dispatch({
          type: ACTION_TYPES.USERS_ERROR,
          payload: result === 'error' ? 'usersError' : 'noUserData'
        });
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_USERS,
        payload: { data, total, info }
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
    fetchData('createfavoriteFilter', filterForPost, 'POST', name).then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.users.unableToSaveAsFavorite');
        return;
      }
      dispatch(getFavorites());
    });
  };
};

const deleteFavorite = (filterId) => {
  return (dispatch) => {
    fetchData('deletefavoriteFilter', null, 'DELETE', filterId).then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.users.unableToDeleteFavorite');
        return;
      }
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

const followUsers = () => {
  return (dispatch) => {
    dispatch(_followUnfollowUsers('followUsers'));
  };
};

const unfollowUsers = () => {
  return (dispatch) => {
    dispatch(_followUnfollowUsers('unfollowUsers'));
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
  deleteFavorite,
  followUsers,
  unfollowUsers
};