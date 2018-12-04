import { INITIATE_ALERT, GET_ALERTS } from './types';
import { fetchData } from './fetch/data';
import { entityId, entityType } from 'entity-details/reducers/entity/selectors';
import { initializeEntityDetails } from './entity-creators';
import { selectedAlertId } from 'entity-details/reducers/alerts/selectors';

export const initializeAlert = (alertId) => {
  return (dispatch, getState) => {
    dispatch({
      type: INITIATE_ALERT,
      payload: alertId
    });
    // Entity ID as query parameter
    if (!getState) {
      return;
    }
    const queryparam = {
      entity_id: entityId(getState()),
      load_comments: true
    };
    fetchData('entityAlerts', queryparam, null, alertId).then(({ data }) => {
      dispatch({
        type: GET_ALERTS,
        payload: data
      });
    });
  };
};

export const notARisk = (dataForPost) => {
  return (dispatch, getState) => {
    const alert = selectedAlertId(getState());
    fetchData('notARisk', dataForPost, 'PATCH', alert).then(() => {
      dispatch(initializeEntityDetails({ entityId: entityId(getState()), entityType: entityType(getState()), alertId: alert }));
    });
  };
};