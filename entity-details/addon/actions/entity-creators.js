import { INITIATE_ENTITY, GET_ENTITY_DETAILS, UPDATE_FOLLOW, ENTITY_ERROR } from './types';
import { fetchData } from './fetch/data';
import { initializeAlert } from './alert-details';
import { initializeIndicator } from './indicator-details';
import { entityId } from 'entity-details/reducers/entity/selectors';
import { lookup } from 'ember-dependency-lookup';

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
    const fetchObj = {
      restEndpointLocation: 'userDetails',
      data: null,
      method: 'GET',
      urlParameters: entityId
    };
    fetchData(fetchObj).then((result) => {
      if (result === 'error') {
        dispatch({
          type: ENTITY_ERROR
        });
      } else {
        const [userDetails] = result.data;
        dispatch({
          type: GET_ENTITY_DETAILS,
          payload: userDetails
        });
        if (!alertId && userDetails) {
          if (userDetails.alerts && userDetails.alerts[0]) {
            dispatch(initializeAlert(userDetails.alerts[0].id));
          }
        }
      }
    });
  };
};

export const followUser = () => {
  return (dispatch, getState) => {
    const entity = entityId(getState());
    const postData = { userIds: [entity] };
    const fetchObj = {
      restEndpointLocation: 'followUser',
      data: postData,
      method: 'POST',
      urlParameters: null
    };
    fetchData(fetchObj).then((result) => {
      if (result === 'error') {
        const flashMessages = lookup('service:flashMessages');
        const i18n = lookup('service:i18n');
        flashMessages.danger(i18n.t('error in following entity'));
      } else {
        dispatch({
          type: UPDATE_FOLLOW,
          payload: true
        });
      }
    });
  };
};

export const unfollowUser = () => {
  return (dispatch, getState) => {
    const entity = entityId(getState());
    const postData = { userIds: [entity] };
    const fetchObj = {
      restEndpointLocation: 'unfollowUser',
      data: postData,
      method: 'POST',
      urlParameters: null
    };
    fetchData(fetchObj).then((result) => {
      if (result === 'error') {
        const flashMessages = lookup('service:flashMessages');
        const i18n = lookup('service:i18n');
        flashMessages.danger(i18n.t('error in un-following entity'));
      } else {
        dispatch({
          type: UPDATE_FOLLOW,
          payload: false
        });
      }
    });
  };
};