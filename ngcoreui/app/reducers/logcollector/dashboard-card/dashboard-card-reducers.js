import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';

const initialState = {
  protocols: '',
  itemKeys: [],
  itemsStatus: 'wait',

  itemEventRateStatus: 'wait',
  itemByteRateStatus: 'wait',
  itemErrorRateStatus: 'wait',

  itemTotalEventsStatus: 'wait',
  itemTotalBytesStatus: 'wait',
  itemTotalErrorsStatus: 'wait',

  itemValueEventRate: {},
  itemValueByteRate: {},
  itemValueErrorRate: {},

  itemValueNumEvents: {},
  itemValueNumBytes: {},
  itemValueNumErrors: {}
};

export default reduxActions.handleActions({

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemKeys: [],
          itemsStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('itemsStatus', 'error');
      },
      success: (state) => {
        let arr = null;
        if (action.payload != null && action.payload.nodes != null) {
          arr = action.payload.nodes.map((x) => ({ protocol: x.name }));
        }
        return state.merge({
          itemKeys: arr,
          itemsStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_EVENT_RATE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemEventRateStatus: 'wait',
          itemValueEventRate: []
        });
      },
      failure: (state) => {
        return state.set('itemEventRateStatus', 'error');
      },
      success: (state) => {
        let value = '';
        let node = null;
        if (action.payload != null) {
          value = action.payload.string;
          if (action.payload.path != null) {
            node = action.payload.path.split('/');
          }
        }
        const protocol = (node != null) ? node[2] : null;
        const mergedObj = { ...state.itemValueEventRate };
        mergedObj[protocol] = parseInt(value, 10);

        return state.merge({
          itemValueEventRate: mergedObj,
          itemEventRateStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_BYTE_RATE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemByteRateStatus: 'wait',
          itemValueByteRate: {}
        });
      },
      failure: (state) => {
        return state.set('itemByteRateStatus', 'error');
      },
      success: (state) => {
        let value = '';
        let node = null;
        if (action.payload != null) {
          value = action.payload.string;
          if (action.payload.path != null) {
            node = action.payload.path.split('/');
          }
        }
        const protocol = (node != null) ? node[2] : null;
        const mergedObj = { ...state.itemValueByteRate };
        mergedObj[protocol] = parseInt(value, 10);
        return state.merge({
          itemValueByteRate: mergedObj,
          itemByteRateStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_ERROR_RATE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemErrorRateStatus: 'wait',
          itemValueErrorRate: {}
        });
      },
      failure: (state) => {
        return state.set('itemErrorRateStatus', 'error');
      },
      success: (state) => {
        let value = '';
        let node = null;
        if (action.payload != null) {
          value = action.payload.string;
          if (action.payload.path != null) {
            node = action.payload.path.split('/');
          }
        }
        const protocol = (node != null) ? node[2] : null;
        const mergedObj = { ...state.itemValueErrorRate };
        mergedObj[protocol] = parseInt(value, 10);
        return state.merge({
          itemValueErrorRate: mergedObj,
          itemErrorRateStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_EVENTS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemTotalEventsStatus: 'wait',
          itemValueNumEvents: {}
        });
      },
      failure: (state) => {
        return state.set('itemTotalEventsStatus', 'error');
      },
      success: (state) => {
        let value = '';
        let node = null;
        if (action.payload != null) {
          value = action.payload.string;
          if (action.payload.path != null) {
            node = action.payload.path.split('/');
          }
        }
        const protocol = (node != null) ? node[2] : null;
        const mergedObj = { ...state.itemValueNumEvents };
        mergedObj[protocol] = parseInt(value, 10);
        return state.merge({
          itemValueNumEvents: mergedObj,
          itemTotalEventsStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_BYTES]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemTotalBytesStatus: 'wait',
          itemValueNumBytes: {}
        });
      },
      failure: (state) => {
        return state.set('itemTotalBytesStatus', 'error');
      },
      success: (state) => {
        let value = '';
        let node = null;
        if (action.payload != null) {
          value = action.payload.string;
          if (action.payload.path != null) {
            node = action.payload.path.split('/');
          }
        }
        const protocol = (node != null) ? node[2] : null;
        const mergedObj = { ...state.itemValueNumBytes };
        mergedObj[protocol] = parseInt(value, 10);
        return state.merge({
          itemValueNumBytes: mergedObj,
          itemTotalBytesStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_ERRORS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          itemTotalErrorsStatus: 'wait',
          itemValueNumErrors: {}
        });
      },
      failure: (state) => {
        return state.set('itemTotalErrorsStatus', 'error');
      },
      success: (state) => {
        let value = '';
        let node = null;
        if (action.payload != null) {
          value = action.payload.string;
          if (action.payload.path != null) {
            node = action.payload.path.split('/');
          }
        }
        const protocol = (node != null) ? node[2] : null;
        const mergedObj = { ...state.itemValueNumErrors };
        mergedObj[protocol] = parseInt(value, 10);
        return state.merge({
          itemValueNumErrors: mergedObj,
          itemTotalErrorsStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
