import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/logs';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  parserRulesStatus: null, // wait, completed, error,
  parserRules: [],
  selectedParserId: null,
  selectedRuleId: null,
  formats: [],
  clickedLog: -1,
  clickedRule: -1
};

export default reduxActions.handleActions({
  [ACTION_TYPES.FIND_ALL]: (state, action) => (
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
        return state.merge(
          {
            parserRules: action.payload.data,
            clickedLog: -1,
            selectedParserId: '',
            selectedRuleId: ''
          }
        );
      }
    })
  ),
  [ACTION_TYPES.GET_FORMATS]: (state, action) => (
    handle(state, action, {
      start: (state) => state,
      failure: (state) => state,
      success: (state) => {
        const theFormats = action.payload.data;
        return state.merge(
          {
            formats: theFormats,
            parserRulesStatus: 'completed'
          }
        );
      }
    })
  ),
  [ACTION_TYPES.SELECT_LOG_PARSER]: (state, { payload }) => {
    return state.merge(
      {
        clickedLog: payload.clickedLog,
        selectedParserId: payload.logName,
        selectedRuleId: '',
        clickedRule: -1
      }
    );
  },
  [ACTION_TYPES.SELECT_PARSER_RULE]: (state, { payload }) => {
    return state.merge(
      {
        clickedRule: payload.clickedRule,
        selectedRuleId: payload.ruleName
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