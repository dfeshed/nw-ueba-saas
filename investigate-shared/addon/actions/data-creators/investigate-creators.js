import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/investigate/investigate';

const getServiceId = (belongsTo) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SET_INVESTIGATE_PREFERENCE,
      promise: api.getInvestigateServiceId(),
      meta: {
        belongsTo
      }
    });
  };
};


export {
  getServiceId
};
