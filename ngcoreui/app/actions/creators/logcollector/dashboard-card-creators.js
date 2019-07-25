import * as ACTION_TYPES from 'ngcoreui/actions/types';
import dashboardCardAPI from 'ngcoreui/actions/api/logcollector/dashboard-card-api';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';

/**
 * Fetches the row data for the log-collector table card
 */
const initializeProtocols = () => {
  return async(dispatch, getState) => {
    await dispatch(fetchProtocols());
    const protocolObjs = dashboardCardSelectors.protocolArray(getState());
    if (protocolObjs == null) {
      return;
    }
    protocolObjs.forEach((protocolObj) => {
      dispatch(fetchProtocolData(protocolObj.protocol));
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
 * Action creator that dispatches actions for fetching protocol's data.
 * @method fetchProtocols
 * @private
 * @returns {function(*, *)}
 */
const fetchProtocolData = (protocolName) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOL_DATA,
    promise: dashboardCardAPI.fetchProtocolData(protocolName)
  };
};

export {
  initializeProtocols,
  fetchProtocols,
  fetchProtocolData
};