import * as ACTION_TYPES from 'configure/actions/types/content';
import api from 'configure/actions/api/content/log-parser-rules';

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

const initializeLogParserRules = () => {
  return (dispatch) => {
    dispatch(_findAllLogParsers());
    dispatch(_fetchRuleFormats());
  };
};

const _fetchRuleFormats = () => {
  return {
    type: ACTION_TYPES.FETCH_FORMATS,
    promise: api.fetchRuleFormats()
  };
};

const _findAllLogParsers = () => {
  return {
    type: ACTION_TYPES.FIND_ALL,
    promise: api.findAllLogParsers()
  };
};

const fetchParserRules = (name) => {
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

const selectLogParser = (index) => {
  return {
    type: ACTION_TYPES.SELECT_LOG_PARSER,
    payload: index
  };
};

export {
  // addParserRule,
  // deleteParserRule,
  initializeLogParserRules,
  fetchParserRules,
  selectParserRule,
  selectLogParser
};