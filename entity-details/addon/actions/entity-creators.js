import { INITIATE_ENTITY, GET_ENTITY_DETAILS } from './types';
import { fetchData } from './fetch/data';

export const initializeEntityDetails = (payload) => {
  return (dispatch) => {
    dispatch({
      type: INITIATE_ENTITY,
      payload
    });
    fetchData('userDetails', null, null, payload.entityId).then(({ data }) => {
      dispatch({
        type: GET_ENTITY_DETAILS,
        payload: data[0]
      });
    });
  };
};