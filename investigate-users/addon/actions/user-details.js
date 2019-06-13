import * as ACTION_TYPES from './types';
import { fetchData } from './fetch/data';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';

const getRiskyUserCount = () => {
  return (dispatch) => {
    fetchData('riskyUserCount').then((result) => {
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
const getWatchedUserCount = () => {
  return (dispatch) => {
    fetchData('watchedUserCount').then((result) => {
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
const getUserOverview = () => {
  return (dispatch) => {
    fetchData('userOverview').then((result) => {
      if (result === 'error' || result.data.length === 0) {
        dispatch({
          type: ACTION_TYPES.TOP_USERS_ERROR,
          payload: result === 'error' ? 'topUsersError' : 'noUserData'
        });
        return;
      }

      dispatch({
        type: ACTION_TYPES.GET_TOP_RISKY_USER,
        payload: result.data
      });
    });
  };
};

const resetUser = () => ({ type: ACTION_TYPES.RESET_USER });

const initiateUser = (payload) => ({ type: ACTION_TYPES.INITIATE_USER, payload });

export {
  getRiskyUserCount,
  getWatchedUserCount,
  getUserOverview,
  resetUser,
  initiateUser
};