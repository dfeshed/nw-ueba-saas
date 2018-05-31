import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null,
        scanStartTime: '10:00',
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  policyList: [],
  policyStatus: null,
  policySaveStatus: null // wait, complete, error
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_POLICY_LIST]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          policyList: [],
          policyStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('policyStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          policyList: action.payload.data,
          policyStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.NEW_POLICY]: (state) => {
    return state.merge({
      policy: { ...initialState.policy },
      policySaveStatus: null
    });
  },

  [ACTION_TYPES.EDIT_POLICY]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    return state.setIn(fields, value);
  },

  [ACTION_TYPES.UPDATE_POLICY_PROPERTY]: (state, action) => state.merge({ policy: action.payload }, { deep: true }),

  [ACTION_TYPES.SAVE_POLICY]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('policySaveStatus', 'wait');
      },
      failure: (state) => {
        return state.set('policySaveStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          policy: action.payload.data,
          policySaveStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
