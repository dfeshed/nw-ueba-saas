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
        selectedParserRuleIndex: -1
      }
    );
  }
  // delete and add parser rules are no implemented and will be added in subsequent PRs
  /*
  [ACTION_TYPES.DELETE_PARSER_RULE]: (state, action) => (
    handle(state, action, {
      start: (state) => state,
      failure: (state) => state,
      success: (state) => {
        const deletedRuleId = action.payload.data;
        return state.set('parserRules', state.parserRules.filter((rule) => rule._id !== deletedRuleId));
      }
    })
  ),
  [ACTION_TYPES.ADD_PARSER_RULE]: (state, action) => (
    handle(state, action, {
      start: (state) => state,
      failure: (state) => state,
      success: (state) => {
        const deletedRuleId = action.payload.data;
        const thisRule = state.parserRules.filter((rule) => rule._id === deletedRuleId);
        return state.set('parserRules', state.parserRules.concat(thisRule));
      }
    })
  )
  */
}, Immutable.from(initialState));