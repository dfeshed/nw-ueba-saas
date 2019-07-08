import * as ACTION_TYPES from 'ngcoreui/actions/types';
import dashboardCardAPI from 'ngcoreui/actions/api/logcollector/dashboard-card-api';

const initializeProtocols = () => {
  return (dispatch) => {
    dispatch(fetchProtocols());
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

export { initializeProtocols };
