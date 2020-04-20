import * as ACTION_TYPES from 'ngcoreui/actions/types';
import dashboardCardAPI from 'ngcoreui/actions/api/logcollector/dashboard-card-api';

/**
 * Fetches the row data for the log-collector table card
 */
const refreshProtocols = () => {
  return (dispatch) => {
    dispatch(fetchEventSourcesStatsData());
  };
};

/**
 * Action creator that dispatches actions for fetching protocol's data.
 * @method fetchEventSourcesStatsData
 * @private
 * @returns {function(*, *)}
 */
const fetchEventSourcesStatsData = () => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA,
    promise: dashboardCardAPI.fetchEventSourcesProtocolData()
  };
};

export {
  refreshProtocols,
  fetchEventSourcesStatsData
};