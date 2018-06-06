import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/content';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  logParsers: [],
  logParserStatus: null, // wait, completed, error,
  parserRules: [],
  parserRulesStatus: null, // wait, completed, error,
  ruleFormats: [],
  selectedParserRuleIndex: -1,
  selectedLogParserIndex: -1,
  selectedFormat: null
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
        return state.merge(
          {
            logParsers: action.payload.data,
            selectedParserRuleIndex: 0
          }
        );
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
        const theRules = action.payload.data;
        const selectedIndex = state.selectedParserRuleIndex;
        return state.merge(
          {
            parserRules: theRules,
            parserRulesStatus: 'completed',
            selectedParserRuleIndex: (theRules.length == selectedIndex) ? selectedIndex : 0,
            selectedFormat: null
          }
        );
      }
    })
  ),

  [ACTION_TYPES.SELECT_PARSER_RULE]: (state, { payload }) => {
    return state.merge(
      {
        selectedParserRuleIndex: payload,
        selectedFormat: null
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
  }
}, Immutable.from(initialState));