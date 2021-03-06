import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';

const initialState = {
  protocols: '',
  itemKeys: [],
  itemKeysStatus: 'wait',

  itemProtocolData: [],
  itemProtocolDataStatus: 'wait',

  esStatsData: [],
  esStatsDataStatus: 'wait',

  tcpRate: '0',
  tcpStreamId: null
};

export default reduxActions.handleActions({
  [ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemKeys: [],
          itemKeysStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('itemKeysStatus', 'error');
      },
      success: (state) => {
        let arr = null;
        if (action.payload != null && action.payload.nodes != null) {
          arr = action.payload.nodes.map((x) => ({ protocol: x.name }));
        }
        return state.merge({
          itemKeys: arr,
          itemKeysStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOL_DATA]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemProtocolDataStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('itemProtocolDataStatus', 'error');
      },
      success: (state) => {
        const allNodes = [];
        action.payload.nodes.forEach((x) => allNodes.push(x));

        const mergedObj = {};
        let protocolName = '';
        if (allNodes.length > 0 && allNodes[0].path != null) {
          protocolName = allNodes[0].path.split('/')[2];
        }
        mergedObj.protocol = protocolName;
        mergedObj.eventRate = '0';
        mergedObj.byteRate = '0';
        mergedObj.errorRate = '0';
        mergedObj.numOfEvents = '0';
        mergedObj.numOfBytes = '0';
        mergedObj.errorCount = '0';

        allNodes.forEach((x) => {
          const property = x.name;
          switch (property) {
            case 'total_events':
              mergedObj.numOfEvents = getLocaleNumberFromString(x.value);
              break;
            case 'total_bytes':
              mergedObj.numOfBytes = getLocaleNumberFromString(x.value);
              break;
            case 'total_errors':
              mergedObj.errorCount = getLocaleNumberFromString(x.value);
              break;
            case 'total_events_rate':
              mergedObj.eventRate = getLocaleNumberFromString(x.value);
              break;
            case 'total_bytes_rate':
              mergedObj.byteRate = getLocaleNumberFromString(x.value);
              break;
            case 'total_errors_rate':
              mergedObj.errorRate = getLocaleNumberFromString(x.value);
          }
        });

        let mergedObjDict = {};
        if (state.itemProtocolData != null) {
          mergedObjDict = { ...state.itemProtocolData };
        }
        mergedObjDict[protocolName] = mergedObj;

        return state.merge({
          itemProtocolData: mergedObjDict,
          itemProtocolDataStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          esStatsData: [],
          esStatsDataStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('esStatsDataStatus', 'error');
      },
      success: (state) => {
        let mergedObjDict = {};
        if (state.esStatsData != null) {
          mergedObjDict = { ...state.esStatsData };
        }
        if (action.payload != null && action.payload.nodes != null) {
          const { nodes: allNodes } = action.payload;
          const protocolSize = 25;
          const totalEventsInd = 19;
          const totalBytesInd = 18;
          const eventRateInd = 8;
          const errorsCountInd = 4;
          for (let i = 0; i < allNodes.length; i += protocolSize) {
            const mergedObj = {};
            mergedObj.protocol = allNodes[i].name;
            mergedObj.numOfEvents = getLocaleNumberFromString(allNodes[i + totalEventsInd].value);
            mergedObj.eventRate = getLocaleNumberFromString(allNodes[i + eventRateInd].value);
            mergedObj.numOfBytes = getLocaleNumberFromString(allNodes[i + totalBytesInd].value);
            mergedObj.errorCount = getLocaleNumberFromString(allNodes[i + errorsCountInd].value);
            mergedObjDict[mergedObj.protocol] = mergedObj;
          }
        }
        return state.merge({
          esStatsData: mergedObjDict,
          esStatsDataStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_UPDATE_TCP_VALUE]: (state, action) => {
    const message = action.payload;
    const tcpVal = (message != null && message.nodes != null) ? message.nodes[0].value : '0';
    return state.merge({
      tcpRate: tcpVal
    });
  },

  [ACTION_TYPES.LOG_COLLECTOR_UPDATE_TCP_TID]: (state, action) => {
    return state.merge({
      tcpStreamId: action.payload
    });
  }

}, Immutable.from(initialState));

const getLocaleNumberFromString = (num) => {
  return parseInt(num, 10).toLocaleString();
};

