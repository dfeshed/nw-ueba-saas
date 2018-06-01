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
  selectedLogParserIndex: -1
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
        const theFormats = action.payload.data;
        return state.merge(
          {
            ruleFormats: theFormats,
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
        return state.merge(
          {
            parserRules: theRules,
            parserRulesStatus: 'completed'
          }
        );
      }
    })
  ),

  [ACTION_TYPES.SELECT_PARSER_RULE]: (state, { payload }) => {
    return state.set('selectedParserRuleIndex', payload);
  },

  [ACTION_TYPES.SELECT_LOG_PARSER]: (state, { payload }) => {
    return state.merge(
      {
        selectedLogParserIndex: payload,
        selectedParserRuleIndex: 0
      }
    );
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