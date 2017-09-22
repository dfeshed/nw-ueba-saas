import * as ACTION_TYPES from './types';

const serviceSelected = (serviceId) => {
  return {
    type: ACTION_TYPES.SERVICE_SELECTED,
    payload: serviceId
  };
};

export {
  serviceSelected
};
