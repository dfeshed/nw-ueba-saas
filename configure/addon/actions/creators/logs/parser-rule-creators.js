import * as ACTION_TYPES from 'configure/actions/types/logs';
import api from 'configure/actions/api/logs/parser-rules';

const deleteRule = (id) => {
  return {
    type: ACTION_TYPES.DELETE_PARSER_RULE,
    promise: api.deleteRule(id)
  };
};

const getFormats = () => {
  return {
    type: ACTION_TYPES.GET_FORMATS,
    promise: api.getFormats()
  };
};

const findAllLogParsers = () => {
  return {
    type: ACTION_TYPES.FIND_ALL,
    promise: api.findAllLogParsers()
  };
};

const getRules = (name) => {
  return {
    type: ACTION_TYPES.FETCH_PARSER_RULES,
    promise: api.getRules(name)
  };
};

const addRule = (id) => {
  return {
    type: ACTION_TYPES.ADD_PARSER_RULE,
    promise: api.addRule(id)
  };
};

const selectParserRule = (name, index) => {
  return {
    type: ACTION_TYPES.SELECT_PARSER_RULE,
    payload: { 'ruleName': name, 'clickedRuleIndex': index }
  };
};

const selectLogParser = (name, index) => {
  return {
    type: ACTION_TYPES.SELECT_LOG_PARSER,
    payload: { 'logName': name, 'clickedLogIndex': index }
  };
};

export {
  deleteRule,
  getFormats,
  getRules,
  addRule,
  findAllLogParsers,
  selectParserRule,
  selectLogParser
};