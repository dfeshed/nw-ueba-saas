import * as ACTION_TYPES from './types';
import { fetchData } from './fetch/data';

const getAdminUserCount = () => {
  return (dispatch) => {
    fetchData('adminUserCount').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_ADMIN_USER_COUNT,
        payload: data
      });
    });
  };
};
const getRiskyUserCount = () => {
  return (dispatch) => {
    fetchData('riskyUserCount').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_RISKY_USER_COUNT,
        payload: data
      });
    });
  };
};
const getWatchedUserCount = () => {
  return (dispatch) => {
    fetchData('watchedUserCount').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_WATCHED_USER_COUNT,
        payload: data
      });
    });
  };
};
const getUserOverview = () => {
  return (dispatch) => {
    fetchData('userOverview').then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.GET_TOP_RISKY_USER,
        payload: data
      });
    });
  };
};

export {
  getAdminUserCount,
  getRiskyUserCount,
  getWatchedUserCount,
  getUserOverview
};