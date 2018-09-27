import * as ACTION_TYPES from '../types';
import { fetchServices, fetchSummary } from 'investigate-shared/actions/api/services';

import moment from 'moment';

export const getDbEndTime = (state) => {
  const { summaryData } = state.processAnalysis.services;
  return summaryData ? summaryData.endTime : null;
};

export const getDbStartTime = (state) => {
  const { summaryData } = state.processAnalysis.services;
  return summaryData ? summaryData.startTime : null;
};
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
              const { serviceId } = getState().processAnalysis.query;
              dispatch(getServiceSummary(serviceId));
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
export const getServiceSummary = (serviceId) => {
  return (dispatch) => {
    if (serviceId) {
      dispatch({
        type: ACTION_TYPES.SUMMARY_RETRIEVE,
        promise: fetchSummary(serviceId),
        meta: {}
      });
    }
  };
};

const _setQueryTimeRange = (dispatch, id, startTime, endTime) => {
  dispatch({
    type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
    payload: {
      startTime,
      endTime,
      selectedTimeRangeId: id
    }
  });
};

/**
 * Takes a time range object and calculates the start and end dates. The
 * timeRange object has the following properties:
 * `{ id: 'LAST_HOUR', name: 'Last 1 Hour', value: 1, unit: 'hours' }`
 * Since we're rounding out to the full minute (start time of 0 seconds and end
 * time of 59 seconds), the final `startTime` has 1 minute added to it so that
 * it most closely represents the desired time range.
 * @param {object} timeRange The time range
 * @public
 */
export const setQueryTimeRange = ({ id, value, unit, startTime, endTime }, isCustomRange) => {
  return (dispatch, getState) => {

    if (!isCustomRange) {
      const state = getState();
      // Get the database start/end times. If they are 0, then use browser time
      // For startTime, set time to 1970 if DB time was 0.
      const dbEndTime = getDbEndTime(state) || moment().unix();
      const dbStartTime = getDbStartTime(state) || moment(0).unix();
      let startTime;

      // DB time
      const endTime = moment(dbEndTime * 1000).endOf('minute');

      if (value) {
        startTime = moment(endTime).subtract(value, unit).add(1, 'minutes').startOf('minute');
        // if the precision is in months - for last 30 days, momentjs takes last 30 days 23 hrs.
        // So adding 23 hrs to startTime to negate the effect.
        if (unit == 'months') {
          startTime = startTime.add(23, 'hours');
        }
      } else {
        startTime = moment(dbStartTime * 1000).startOf('minute');
      }
      _setQueryTimeRange(dispatch, id, startTime.unix(), endTime.unix());
    } else {
      _setQueryTimeRange(dispatch, 'CUSTOM', startTime / 1000, endTime / 1000);
    }
  };
};
export const setSelectedService = (service) => {
  return (dispatch, getState) => {
    const { serviceId } = getState().processAnalysis.query;
    if (serviceId !== service.id) {
      dispatch({ type: ACTION_TYPES.SERVICE_SELECTED, payload: service.id });
      dispatch(getServiceSummary(service.id));
    }
  };
};
