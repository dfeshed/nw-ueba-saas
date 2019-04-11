import { INITIATE_ALERT, GET_ALERTS, RESET_ALERT, UPDATE_SORT } from './types';
import { fetchData } from './fetch/data';
import { entityId, entityType } from 'entity-details/reducers/entity/selectors';
import { initializeEntityDetails } from './entity-creators';
import { selectedAlertId } from 'entity-details/reducers/alerts/selectors';

export const resetAlerts = () => ({ type: RESET_ALERT });

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
    const fetchObj = {
      restEndpointLocation: 'entityAlerts',
      data: queryparam,
      method: 'GET',
      urlParameters: alertId
    };
    fetchData(fetchObj).then(({ data }) => {
      dispatch({
        type: GET_ALERTS,
        payload: data
      });
    });
  };
};

export const alertIsNotARisk = (dataForPost) => {
  return (dispatch, getState) => {
    const alert = selectedAlertId(getState());
    const fetchObj = {
      restEndpointLocation: 'notARisk',
      data: dataForPost,
      method: 'PATCH',
      urlParameters: alert
    };
    fetchData(fetchObj).then(() => {
      dispatch(initializeEntityDetails({ entityId: entityId(getState()), entityType: entityType(getState()), alertId: alert }));
    });
  };
};

export const updateSort = (id) => ({ type: UPDATE_SORT, payload: id });