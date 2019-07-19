import * as ACTION_TYPES from 'ngcoreui/actions/types';
import dashboardCardAPI from 'ngcoreui/actions/api/logcollector/dashboard-card-api';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';

/**
 * Fetches the row data for the log-collector table card
 */
const initializeProtocols = () => {
  return (dispatch, getState) => {
    dispatch(fetchProtocols())
      .then(() => {
        const protocolObjs = dashboardCardSelectors.protocolArray(getState());
        if (protocolObjs == null) {
          return;
        }
        protocolObjs.forEach((protocolObj) => {
          dispatch(fetchEventRate(protocolObj))
            .then(() => {
              const eventRateFailure = dashboardCardSelectors.isProtocolEventRateLoadingFailed(getState());
              // if the head node doesn't exist, don't call the api of other columns as well
              if (!eventRateFailure) {
                dispatch(fetchBytesRate(protocolObj));
                dispatch(fetchErrorsRate(protocolObj));
                dispatch(fetchTotalEvents(protocolObj));
                dispatch(fetchTotalBytes(protocolObj));
                dispatch(fetchTotalErrors(protocolObj));
              }
            });
        });
      });
  };
};

/**
 * Action creator that dispatches a set of actions for fetching protocols.
 * @method fetchProtocols
 * @private
 * @returns {function(*, *)}
 */
const fetchProtocols = () => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS,
    promise: dashboardCardAPI.fetchProtocolList()
  };
};

/**
 * Action creator that dispatches a set of actions for fetching event rate.
 * @method fetchEventRate
 * @private
 * @returns {function(*, *)}
 */
const fetchEventRate = (protocolObj) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_EVENT_RATE,
    promise: dashboardCardAPI.fetchEventRate(protocolObj)
  };
};

/**
 * Action creator that dispatches a set of actions for fetching byte rate.
 * @method fetchProtocols
 * @private
 * @returns {function(*, *)}
 */
const fetchBytesRate = (protocolObj) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_BYTE_RATE,
    promise: dashboardCardAPI.fetchByteRate(protocolObj)
  };
};

/**
 * Action creator that dispatches a set of actions for fetching error rate.
 * @method fetchErrorsRate
 * @private
 * @returns {function(*, *)}
 */
const fetchErrorsRate = (protocolObj) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_ERROR_RATE,
    promise: dashboardCardAPI.fetchErrorRate(protocolObj)
  };
};

/**
 * Action creator that dispatches a set of actions for fetching the number of events.
 * @method fetchTotalEvents
 * @private
 * @returns {function(*, *)}
 */
const fetchTotalEvents = (protocolObj) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_EVENTS,
    promise: dashboardCardAPI.fetchTotalEvents(protocolObj)
  };
};

/**
 * Action creator that dispatches a set of actions for fetching the number of bytes.
 * @method fetchTotalBytes
 * @private
 * @returns {function(*, *)}
 */
const fetchTotalBytes = (protocolObj) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_BYTES,
    promise: dashboardCardAPI.fetchTotalBytes(protocolObj)
  };
};

/**
 * Action creator that dispatches a set of actions for fetching the number of errors.
 * @method fetchTotalErrors
 * @private
 * @returns {function(*, *)}
 */
const fetchTotalErrors = (protocolObj) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_ERRORS,
    promise: dashboardCardAPI.fetchTotalErrors(protocolObj)
  };
};

export { initializeProtocols, fetchProtocols, fetchEventRate, fetchBytesRate, fetchTotalErrors, fetchTotalBytes,
  fetchTotalEvents, fetchErrorsRate };