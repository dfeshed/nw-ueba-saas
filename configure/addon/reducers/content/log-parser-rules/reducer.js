import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  logParsers: [],
  logParserStatus: null, // wait, completed, error,
  deviceTypes: [],
  deviceTypesStatus: null, // wait, completed, error
  deviceClasses: [],
  deviceClassesStatus: null, // wait, completed, error
  parserRules: [],
  parserRulesStatus: null, // wait, completed, error,
  ruleFormats: [],
  selectedParserRuleIndex: 0,
  selectedLogParserIndex: 0,
  selectedFormat: null,
  isTransactionUnderway: false,
  parserRuleTokens: []
};

export default reduxActions.handleActions({
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
      start: (state) => state,
      failure: (state) => {
        return state.set('logParserStatus', 'error');
      },
      success: (state) => {
        return state.merge(
          {
            ruleFormats: action.payload.data,
            logParserStatus: 'completed'
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
          parserRulesStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('parserRulesStatus', 'error');
      },
      success: (state) => {
        const rules = action.payload.data;
        const selectedIndex = state.selectedParserRuleIndex;
        return state.merge(
          {
            parserRules: rules,
            parserRulesStatus: 'completed',
            selectedFormat: null,
            parserRuleTokens: rules[selectedIndex].literals
          }
        );
      }
    })
  ),

  [ACTION_TYPES.SELECT_PARSER_RULE]: (state, { payload }) => {
    const rules = state.parserRules;
    return state.merge(
      {
        selectedParserRuleIndex: payload,
        selectedFormat: null,
        parserRuleTokens: rules[payload].literals
      }
    );
  },

  [ACTION_TYPES.SELECT_FORMAT_VALUE]: (state, { payload }) => {
    return state.set('selectedFormat', payload);
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
  [ACTION_TYPES.ADD_RULE_TOKEN]: (state, { payload }) => {
    const newToken = [{ 'value': payload }];
    const ruleTokens = state.parserRuleTokens;
    const tokenExists = ruleTokens.some((tkn) => tkn.value === payload);
    if (!tokenExists && payload !== '') {
      return state.set('parserRuleTokens', newToken.concat(ruleTokens));
    } else {
      return state;
    }
  },

  [ACTION_TYPES.EDIT_RULE_TOKEN]: (state, { payload }) => {
    const item = payload.index;
    const newToken = { 'value': payload.token };
    const ruleTokens = state.parserRuleTokens;
    const tokenExists = ruleTokens.some((tkn) => tkn.value === payload.token);
    if (!tokenExists && payload.token !== '') {
      return state.set('parserRuleTokens', ruleTokens.map((token, index) => index === item ? newToken : token));
    } else {
      return state;
    }
  },

  [ACTION_TYPES.DELETE_RULE_TOKEN]: (state, { payload }) => {
    const ruleTokens = state.parserRuleTokens;
    return state.set('parserRuleTokens', ruleTokens.filter((token, index) => index !== payload));
  }

}, Immutable.from(initialState));