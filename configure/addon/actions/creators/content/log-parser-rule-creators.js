import * as ACTION_TYPES from 'configure/actions/types/content';
import api from 'configure/actions/api/content/log-parser-rules';

import {
  filterDeletedRule,
  selectedLogParserName } from 'configure/reducers/content/log-parser-rules/selectors';

/*
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

const initializeLogParserRules = () => {
  return (dispatch) => {
    dispatch(_fetchRuleFormats());
    dispatch(_findAllLogParsers());
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

const _deleteParserRule = (logParserName, filteredRule) => {
  return {
    type: ACTION_TYPES.DELETE_PARSER_RULE,
    promise: api.deleteParserRule(logParserName, filteredRule)
  };
};

const deleteParserRule = () => {
  return (dispatch, getState) => {
    const logParserName = selectedLogParserName(getState());
    const filteredRule = filterDeletedRule(getState());
    dispatch(_deleteParserRule(logParserName, filteredRule));
  };
};

export {
  // addParserRule,
  deleteParserRule,
  initializeLogParserRules,
  selectLogParser,
  selectParserRule
};