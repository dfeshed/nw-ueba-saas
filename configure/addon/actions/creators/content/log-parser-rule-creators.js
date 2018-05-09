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

const fetchRuleFormats = () => {
  return {
    type: ACTION_TYPES.FETCH_FORMATS,
    promise: api.fetchRuleFormats()
  };
};

const findAllLogParsers = () => {
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

const selectParserRule = (name, index) => {
  return {
    type: ACTION_TYPES.SELECT_PARSER_RULE,
    payload: { 'parserRuleName': name, 'clickedParserRuleIndex': index }
  };
};

const selectLogParser = (name, index) => {
  return {
    type: ACTION_TYPES.SELECT_LOG_PARSER,
    payload: { 'logParserName': name, 'clickedLogParserIndex': index }
  };
};

export {
  // addParserRule,
  // deleteParserRule,
  fetchRuleFormats,
  fetchParserRules,
  findAllLogParsers,
  selectParserRule,
  selectLogParser
};