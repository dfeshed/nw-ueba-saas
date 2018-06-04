import * as ACTION_TYPES from '../types';
import { fetchServices, fetchSummary } from 'investigate-shared/actions/api/services';

/**
 * Retrieves the list of services (aka endpoints). This list shouldn't really
 * change much.
 * @return {function} A Redux thunk
 * @public
 */
export const getServices = () => {
  return (dispatch, getState) => {
    const { serviceData } = getState().processAnalysis.services;
    if (!serviceData) {
      dispatch({
        type: ACTION_TYPES.SERVICES_RETRIEVE,
        promise: fetchServices(),
        meta: {
          onSuccess(response) {
            const { data } = response;
            if (data && Array.isArray(data)) {
              const { serviceId } = getState().processAnalysis.processTree.queryInput;
              if (!serviceId) {
                // grab first service in array if one isn't already selected
                const [ service ] = data;
                dispatch({
                  type: ACTION_TYPES.SERVICE_SELECTED,
                  payload: service.id
                });
              }
              dispatch(getServiceSummary());
            }
          }
        }
      });
    }
  };
};

/**
 * Get attribute summary for a selected service. Results include aggregation
 * times that change frequently. So we are not caching these results and instead
 * making a server call everytime.
 * @return {function} A Redux thunk
 * @public
 */
export const getServiceSummary = () => {
  return (dispatch, getState) => {
    const { serviceId } = getState().processAnalysis.processTree.queryInput;
    if (serviceId) {
      dispatch({
        type: ACTION_TYPES.SUMMARY_RETRIEVE,
        promise: fetchSummary(serviceId),
        meta: {}
      });
    }
  };
};
export const setSelectedService = (service) => {
  return (dispatch, getState) => {
    const { serviceId } = getState().processAnalysis.processTree.queryInput;
    if (serviceId !== service.id) {
      dispatch(getServiceSummary());
    }
  };
};
