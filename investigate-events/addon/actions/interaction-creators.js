import * as ACTION_TYPES from './types';

const serviceSelected = (endpointId) => {
  return {
    type: ACTION_TYPES.SERVICE_SELECTED,
    payload: endpointId
  };
};

export {
  serviceSelected
};
