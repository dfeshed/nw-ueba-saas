import { INITIATE_ENTITY, INITIATE_ALERT, INITIATE_INDICATOR, GET_ENTITY_DETAILS } from './types';
import { fetchData } from './fetch/data';

export const initializeEntityDetails = ({ entityId, entityType, alertId, indicatorId }) => {
  return (dispatch) => {
    dispatch({
      type: INITIATE_ENTITY,
      payload: { entityId, entityType }
    });
    if (alertId) {
      dispatch(initializeALert(alertId));
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
          dispatch(initializeALert(userDetails.alerts[0].id));
        }
      }
    });
  };
};

export const initializeALert = (alertId) => {
  return (dispatch) => {
    dispatch({
      type: INITIATE_ALERT,
      payload: alertId
    });
  };
};

export const initializeIndicator = (indicatorId) => {
  return (dispatch) => {
    dispatch({
      type: INITIATE_INDICATOR,
      payload: indicatorId
    });
  };
};