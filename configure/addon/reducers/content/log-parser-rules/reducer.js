import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const baselineSampleLog = 'May 5 2010 15:55:49 switch : %ACE-4-400000: IDS:1000 IP Option Bad Option List by user admin@test.com ' +
  'from 10.100.229.59 to 224.0.0.22 on port 12345. \n\nApr 29 2010 03:15:34 pvg1-ace02: %ACE-3-251008: Health probe failed ' +
  'for server 218.83.175.75:81, connectivity error: server open timeout (no SYN ACK) domain google.com  with mac 06-00-00-00-00-00.';

const initialState = {
  logParsers: [],
  logParserStatus: null, // wait, completed, error,

  parserRules: [],
  parserRulesStatus: null, // wait, completed, error,

  selectedParserRuleIndex: 0,
  selectedLogParserIndex: 0,

  isTransactionUnderway: false,
  sampleLogs: baselineSampleLog,
  sampleLogsStatus: null, // wait, completed, error

  // Dictionaries
  ruleFormats: [],
  ruleFormatsStatus: null, // wait, completed, error
  deviceTypes: [],
  deviceTypesStatus: null, // wait, completed, error
  deviceClasses: [],
  deviceClassesStatus: null, // wait, completed, error
  metas: [],
  metasStatus: null // wait, completed, error
};

export default reduxActions.handleActions({

  [ACTION_TYPES.HIGHLIGHT_SAMPLE_LOGS]: (state, action) => (

    handle(state, action, {
      start: (state) => state.set('sampleLogsStatus', 'wait'),
      failure: (state) => state.set('sampleLogsStatus', 'error'),
      success: (state) => {
        return state.merge(
          {
            sampleLogs: action.payload.data || baselineSampleLog,
            sampleLogsStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.FIND_ALL]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          logParsers: [],
          logParserStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('logParserStatus', 'error');
      },
      success: (state) => {
        return state.set('logParsers', action.payload.data);
      }
    })
  ),
  [ACTION_TYPES.FETCH_FORMATS]: (state, action) => (
    handle(state, action, {
      start: (state) => state.set('ruleFormatsStatus', 'wait'),
      failure: (state) => {
        return state.set('ruleFormatsStatus', 'error');
      },
      success: (state) => {
        return state.merge(
          {
            ruleFormats: action.payload.data,
            ruleFormatsStatus: 'completed'
          }
        );
      }
    })
  ),
  [ACTION_TYPES.FETCH_PARSER_RULES]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          parserRules: [],
          selectedParserRuleIndex: 0,
          parserRulesStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('parserRulesStatus', 'error');
      },
      success: (state) => {
        const rules = action.payload.data;
        return state.merge(
          {
            parserRules: rules,
            parserRulesStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.SELECT_PARSER_RULE]: (state, { payload }) => {
    return state.merge(
      {
        selectedParserRuleIndex: payload
      }
    );
  },

  [ACTION_TYPES.SELECT_LOG_PARSER]: (state, { payload }) => {
    return state.set('selectedLogParserIndex', payload);
  },

  [ACTION_TYPES.DELETE_PARSER_RULE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('deleteRuleStatus', 'wait');
      },
      failure: (state) => {
        return state.set('deleteRuleStatus', 'error');
      },
      success: (state) => {
        const selectedIndex = state.selectedParserRuleIndex;
        const allRules = state.parserRules;
        return state.merge(
          {
            parserRules: allRules.filter((rule, index) => index !== selectedIndex),
            selectedParserRuleIndex: 0,
            deleteRuleStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.SAVE_PARSER_RULE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('saveRuleStatus', 'wait');
      },
      failure: (state) => {
        return state.set('saveRuleStatus', 'error');
      },
      success: (state) => {
        return state.merge(
          {
            selectedParserRuleIndex: 0,
            saveRuleStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.ADD_NEW_PARSER_RULE]: (state, { payload }) => {
    const allRules = state.parserRules;
    return state.merge(
      {
        selectedParserRuleIndex: allRules.length,
        parserRules: allRules.concat({
          name: payload,
          literals: [],
          pattern: {
            captures: [
              {
              }
            ],
            regex: ''
          },
          ruleMetas: [],
          dirty: true,
          outOfBox: false,
          override: false
        })
      }
    );
  },

  [ACTION_TYPES.DEPLOY_LOG_PARSER]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('deployLogParserStatus', 'wait');
      },
      failure: (state) => {
        return state.set('deployLogParserStatus', 'error');
      },
      success: (state) => {
        return state.set('deployLogParserStatus', 'completed');
      }
    })
  ),

  [ACTION_TYPES.FETCH_DEVICE_TYPES]: (state, action) => (
    handle(state, action, {
      start: (state) => state.merge({
        deviceTypesStatus: 'wait',
        deviceTypes: []
      }),
      failure: (state) => state.set('deviceTypesStatus', 'error'),
      success: (state) => {
        const { payload: { data: deviceTypes } } = action;
        return state.merge(
          {
            deviceTypes: [{ }, ...deviceTypes],
            deviceTypesStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.FETCH_DEVICE_CLASSES]: (state, action) => (
    handle(state, action, {
      start: (state) => state.merge({
        deviceClassesStatus: 'wait',
        deviceClasses: []
      }),
      failure: (state) => state.set('deviceClassesStatus', 'error'),
      success: (state) => {
        const { payload: { data: deviceClasses } } = action;
        return state.merge(
          {
            deviceClasses,
            deviceClassesStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.FETCH_METAS]: (state, action) => (
    handle(state, action, {
      start: (state) => state.merge({
        metasStatus: 'wait',
        metas: []
      }),
      failure: (state) => state.set('metasStatus', 'error'),
      success: (state) => {
        const { payload: { data: metas } } = action;
        return state.merge(
          {
            metas,
            metasStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.ADD_LOG_PARSER]: (state, action) => (
    handle(state, action, {
      start: (state) => state.set('isTransactionUnderway', true),
      failure: (state) => state.set('isTransactionUnderway', false),
      success: (state) => {
        const { payload: { data: addedParser } } = action;
        return state.merge(
          {
            logParsers: [...state.logParsers, addedParser],
            selectedLogParserIndex: state.logParsers.length,
            isTransactionUnderway: false
          }
        );
      }
    })
  ),
  [ACTION_TYPES.DELETE_LOG_PARSER]: (state, action) => (
  handle(state, action, {
    start: (state) => state.set('isTransactionUnderway', true),
    failure: (state) => state.set('isTransactionUnderway', false),
    success: (state) => {
      const { payload: { data } } = action;
      return state.merge(
        {
          logParsers: state.logParsers.filter((parser) => parser.name !== data.name),
          selectedLogParserIndex: 0,
          isTransactionUnderway: false
        }
      );
    }
  })
  ),
  [ACTION_TYPES.UPDATE_SELECTED_PARSER_RULE]: (state, { payload: newRule }) => {
    return state.set('parserRules', state.parserRules.map((rule, index) => index === state.selectedParserRuleIndex ? newRule : rule));
  }
}, Immutable.from(initialState));