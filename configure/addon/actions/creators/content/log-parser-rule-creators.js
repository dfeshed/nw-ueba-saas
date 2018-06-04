import * as ACTION_TYPES from 'configure/actions/types/content';
import api from 'configure/actions/api/content/log-parser-rules';

import {
  filterDeletedRule,
  selectedLogParserName,
  parserRules } from 'configure/reducers/content/log-parser-rules/selectors';

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
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.SELECT_LOG_PARSER,
      payload: index
    });
    const logParserName = selectedLogParserName(getState());
    dispatch(fetchParserRules(logParserName));
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

const addNewParserRule = (name) => {
  return {
    type: ACTION_TYPES.ADD_NEW_PARSER_RULE,
    payload: name
  };
};

const _saveParserRule = (logParserName, rules) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_PARSER_RULE,
      promise: api.saveParserRule(logParserName, rules),
      meta: {
        onSuccess() {
          dispatch(fetchParserRules(logParserName));
        }
      }
    });
  };
};
const saveParserRule = () => {
  return (dispatch, getState) => {
    const logParserName = selectedLogParserName(getState());
    const rules = parserRules(getState());
    dispatch(_saveParserRule(logParserName, rules));
  };
};

export {
  addNewParserRule,
  deleteParserRule,
  initializeLogParserRules,
  selectLogParser,
  selectParserRule,
  fetchParserRules,
  saveParserRule
};