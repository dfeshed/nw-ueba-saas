import * as ACTION_TYPES from 'configure/actions/types/content';
import api from 'configure/actions/api/content/log-parser-rules';

import { selectedLogParserName } from 'configure/reducers/content/log-parser-rules/selectors';

/* const deleteParserRule = (id) => {
  return {
    type: ACTION_TYPES.DELETE_PARSER_RULE,
    promise: api.deleteParserRule(id)
  };
};
const addParserRule = (id) => {
  return {
    type: ACTION_TYPES.ADD_PARSER_RULE,
    promise: api.addParserRule(id)
  };
}; */

const _fetchRuleFormats = () => {
  return {
    type: ACTION_TYPES.FETCH_FORMATS,
    promise: api.fetchRuleFormats()
  };
};

const _findAllLogParsers = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FIND_ALL,
      promise: api.findAllLogParsers(),
      meta: {
        onSuccess() {
          dispatch(selectLogParser(0));
        }
      }
    });
  };
};

const _fetchParserRules = (name) => {
  return {
    type: ACTION_TYPES.FETCH_PARSER_RULES,
    promise: api.fetchParserRules(name)
  };
};

const selectParserRule = (index) => {
  return {
    type: ACTION_TYPES.SELECT_PARSER_RULE,
    payload: index
  };
};

const initializeLogParserRules = () => {
  return (dispatch) => {
    dispatch(_findAllLogParsers());
    dispatch(_fetchRuleFormats());
  };
};

const selectLogParser = (index) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.SELECT_LOG_PARSER,
      payload: index
    });
    const logParserName = selectedLogParserName(getState());
    dispatch(_fetchParserRules(logParserName));
  };
};

export {
  // addParserRule,
  // deleteParserRule,
  initializeLogParserRules,
  selectLogParser,
  selectParserRule
};