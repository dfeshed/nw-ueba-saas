import * as ACTION_TYPES from './types';
import { fetchData } from './fetch/data';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';
import { getUserFilter, trendRange, sortOnTrending } from 'investigate-users/reducers/users/selectors';

const getRiskyUserCount = (entityType = 'userId') => {
  return (dispatch) => {
    fetchData('riskyUserCount', { entityType }).then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.errorMessages.unableToGetRiskyUserCount');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_RISKY_USER_COUNT,
        payload: result.data
      });
    });
  };
};
const getWatchedUserCount = (entityType = 'userId') => {
  return (dispatch) => {
    fetchData('watchedUserCount', { entityType }).then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.errorMessages.unableToGetWatchedUserCount');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_WATCHED_USER_COUNT,
        payload: result.data
      });
    });
  };
};

const getTotalCount = (entityType = 'userId') => {
  return (dispatch) => {
    fetchData('totalCount', { entityType }).then((result) => {
      if (result === 'error') {
        flashErrorMessage('investigateUsers.errorMessages.unableToGetTotalUserCount');
        return;
      }
      dispatch({
        type: ACTION_TYPES.GET_TOTAL_USER_COUNT,
        payload: result.data
      });
    });
  };
};

const getUserOverview = (entityType = 'userId') => {
  return (dispatch, getState) => {
    const sortTrending = sortOnTrending(getState());
    const trend = trendRange(getState());
    const additionalFilter = { entityType, sort_field: 'score' };
    if (sortTrending) {
      additionalFilter.sort_field = trend.key === 'daily' ? 'DAILY_TRENDS' : 'WEEKLY_TRENDS';
    }
    fetchData('userOverview', additionalFilter).then((result) => {
      if (result === 'error' || result.data.length === 0) {
        dispatch({
          type: ACTION_TYPES.TOP_USERS_ERROR,
          payload: result === 'error' ? 'topUsersError' : 'noUserData'
        });
        return;
      }

      dispatch({
        type: ACTION_TYPES.GET_TOP_RISKY_USER,
        payload: result
      });
    });
  };
};

const updateEntityType = (entityType) => {
  return (dispatch, getState) => {
    const filter = getUserFilter(getState()).merge({ entityType });
    dispatch(updateFilter(filter, true));
    dispatch(getRiskyUserCount(entityType));
    dispatch(getWatchedUserCount(entityType));
    dispatch(getTotalCount(entityType));
    dispatch(getUserOverview(entityType));
  };
};

const updateSortTrend = () => {
  return (dispatch, getState) => {
    const { entityType } = getUserFilter(getState());
    dispatch({ type: ACTION_TYPES.SORT_ON_TREND });
    dispatch(getUserOverview(entityType));
  };
};

const updateTrendRange = (payload) => {
  return (dispatch, getState) => {
    const { entityType } = getUserFilter(getState());
    dispatch({ type: ACTION_TYPES.UPDATE_TREND_RANGE, payload });
    dispatch(getUserOverview(entityType));
  };
};

const resetUser = () => ({ type: ACTION_TYPES.RESET_USER });

const initiateUser = (payload) => ({ type: ACTION_TYPES.INITIATE_USER, payload });

export {
  getRiskyUserCount,
  getWatchedUserCount,
  getUserOverview,
  resetUser,
  updateEntityType,
  initiateUser,
  updateTrendRange,
  updateSortTrend,
  getTotalCount
};