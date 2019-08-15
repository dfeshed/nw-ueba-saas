import * as ACTION_TYPES from 'ngcoreui/actions/types';
import dashboardCardAPI from 'ngcoreui/actions/api/logcollector/dashboard-card-api';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import { lookup } from 'ember-dependency-lookup';

/**
 * Fetches the row data for the log-collector table card
 */
const refreshProtocols = () => {
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

const stopStreamingTid = (tcpStreamId) => {
  const transport = lookup('service:transport');
  transport.stopStream(tcpStreamId);
};

const stopStreamingTcpRate = () => {
  return (dispatch, getState) => {
    const tid = dashboardCardSelectors.getTCPTid(getState());
    dispatch(stopStreamingTid(tid));
  };
};

const updateTcpTid = (tid) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_UPDATE_TCP_TID,
    payload: tid
  };
};

const updateTcpValue = (value) => {
  return {
    type: ACTION_TYPES.LOG_COLLECTOR_UPDATE_TCP_VALUE,
    payload: value
  };
};

const startStreamingTcpRate = () => {
  const path = '/event-processors/logdecoder/stats/destinations/logdecoder/tcp_connector_events_written_rate';
  return (dispatch) => {
    dispatch(getTcpRate(path, {
      onSuccess: (message) => {
        dispatch(updateTcpValue(message));
      },
      onInit: (tid) => {
        dispatch(updateTcpTid(tid));
      },
      onError: (errorMessage) => {
        throw new Error(errorMessage);
      }
    }));
  };
};

const NOOP = () => {};

const getTcpRate = (path, { onSuccess = NOOP, onError = NOOP, onInit = NOOP }) => {
  const transport = lookup('service:transport');
  return (dispatch, getState) => {
    transport.stopStream(getState().logcollector.tcpStreamId);
    const tid = transport.stream({
      path,
      message: {
        message: 'mon'
      },
      messageCallback: (message) => onSuccess(message),
      errorCallback: onError
    });
    onInit(tid);
  };
};

export {
  refreshProtocols,
  fetchProtocols,
  fetchProtocolData,
  startStreamingTcpRate,
  stopStreamingTcpRate,
  updateTcpTid,
  updateTcpValue
};