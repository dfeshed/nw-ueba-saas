import { INITIATE_ENTITY, GET_ENTITY_DETAILS, UPDATE_FOLLOW } from './types';
import { fetchData } from './fetch/data';
import { initializeAlert } from './alert-details';
import { initializeIndicator } from './indicator-details';
import { entityId } from 'entity-details/reducers/entity/selectors';

export const initializeEntityDetails = ({ entityId, entityType, alertId, indicatorId }) => {
  return (dispatch) => {
    dispatch({
      type: INITIATE_ENTITY,
      payload: { entityId, entityType }
    });
    if (alertId) {
      dispatch(initializeAlert(alertId));
    }
    if (indicatorId) {
      dispatch(initializeIndicator(indicatorId));
    }
    fetchData('userDetails', null, null, entityId).then(({ data }) => {
      const [userDetails] = data;
      dispatch({
        type: GET_ENTITY_DETAILS,
        payload: userDetails
      });
      if (!alertId && userDetails) {
        if (userDetails.alerts && userDetails.alerts[0]) {
          dispatch(initializeAlert(userDetails.alerts[0].id));
        }
      }
    });
  };
};

export const followUser = () => {
  return (dispatch, getState) => {
    const entity = entityId(getState());
    const postData = { userIds: [entity] };
    fetchData('followUser', postData, 'POST').then(() => {
      dispatch({
        type: UPDATE_FOLLOW,
        payload: true
      });
    });
  };
};

export const unfollowUser = () => {
  return (dispatch, getState) => {
    const entity = entityId(getState());
    const postData = { userIds: [entity] };
    fetchData('unfollowUser', postData, 'POST').then(() => {
      dispatch({
        type: UPDATE_FOLLOW,
        payload: false
      });
    });
  };
};