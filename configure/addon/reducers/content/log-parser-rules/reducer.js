import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

export const baselineSampleLog = 'date=2017-08-12 type=traffic subtype=violation user=matt status=deny src=192.168.24.49 dst=192.56.43.56 dstdomain=com sent=0 src_port=4135 dst_port=443 smac=06-02-00-00-00-00 dmac=06-02-00-00-00-01 src_int=internal dst_int=external group=SSO_Guest_Users proto=6 rcvd=583\n\n' +
'May 5 2018 15:55:49 switch : %ACE-4-4000: IDS:1000 IP Option Bad Option user: admin@test.com from 10.100.229.59 port 12345.\n\n' +
'%IIS-4-440: 2017-08-12 13:53:34 192.170.28.192 - W4S31 url=https://test.domain.edu/exchange GET /exchweb/bin/auth/owalogon.asp 440\n\n' +
'Dec 20 13:20:20 instance1 info mod=mail from=matt@rsa.com to=alex@dell.com\n\n';

const initialState = {
  logParsers: [],
  logParserStatus: null, // wait, completed, error,

  parserRules: [],
  parserRulesStatus: null, // wait, completed, error,
  parserRulesOriginal: [], // a copy of the fetched parserRules for identifying changes
  deletedRules: [], // list of deleted rules prior to saving

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
        const { payload: { data = [] } } = action;
        const requestSampleLogs = action.payload.request ? action.payload.request.logs.logs[0] : baselineSampleLog;
        return state.merge(
          {
            // if no data from api, set sampleLogs state to what ever is in the request or baselineSampleLog
            sampleLogs: data.length ? data : requestSampleLogs,
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
        return state.set('logParsersStatus', 'error');
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
          parserRulesStatus: 'wait',
          parserRules: [],
          parserRulesOriginal: [],
          selectedParserRuleIndex: 0
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
            parserRulesOriginal: rules,
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
    return state.merge(
      {
        selectedLogParserIndex: payload,
        sampleLogsStatus: 'wait'
      }
    );
  },

  [ACTION_TYPES.DELETE_PARSER_RULE]: (state) => {
    const selectedRule = state.parserRules[state.selectedParserRuleIndex];
    return state.merge(
      {
        parserRules: state.parserRules.filter((rule) => rule !== selectedRule),
        selectedParserRuleIndex: 0,
        deletedRules: [...state.deletedRules, selectedRule]
      }
    );
  },

  [ACTION_TYPES.SAVE_PARSER_RULE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('saveRuleStatus', 'wait');
      },
      failure: (state) => {
        return state.set('saveRuleStatus', 'error');
      },
      success: (state) => {
        const selectedParser = state.logParsers[state.selectedLogParserIndex];
        return state.merge(
          {
            saveRuleStatus: 'completed',
            parserRulesOriginal: state.parserRules, // once saved, the parserRulesOriginal and the parserRules should be the same
            deletedRules: [],
            logParsers: state.logParsers.map((parser) => parser !== selectedParser ? parser : parser.set('dirty', true))
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
            captures: [],
            regex: '',
            format: null
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
        const { payload: { parserRules, logDeviceParser } } = action;
        // update the parser in the logParsers list with the latest information from the server
        const logParsers = state.logParsers.map((parser, index) => index === state.selectedLogParserIndex ? logDeviceParser : parser);
        return state.merge({
          deployLogParserStatus: 'completed',
          parserRules,
          logParsers,
          parserRulesOriginal: parserRules // once saved, the parserRulesOriginal and the parserRules should be the same
        });
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
  },
  [ACTION_TYPES.DISCARD_RULE_CHANGES]: (state) => {
    return state.merge({
      parserRules: state.parserRulesOriginal,
      selectedParserRuleIndex: state.parserRulesOriginal.length <= state.selectedParserRuleIndex ? 0 : state.selectedParserRuleIndex,
      deletedRules: []
    });
  }

}, Immutable.from(initialState));