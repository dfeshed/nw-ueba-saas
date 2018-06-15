import * as ACTION_TYPES from 'configure/actions/types/content';
import api from 'configure/actions/api/content/log-parser-rules';
import { success, failure } from 'configure/sagas/flash-messages';

import {
  filterDeletedRule,
  selectedLogParserName,
  sampleLogs,
  validRules,
  parserRules } from 'configure/reducers/content/log-parser-rules/selectors';

const defaultCallbacks = {
  onSuccess() {},
  onFailure() {}
};

const addLogParser = (logParser = {}, callbacks = defaultCallbacks) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_LOG_PARSER,
      promise: api.addLogParser(logParser),
      meta: {
        onSuccess() {
          callbacks.onSuccess();
          // fetch the parser rules for the newly created parser
          dispatch(fetchParserRules(logParser.logDeviceParserName));
        },
        onFailure: callbacks.onFailure
      }
    });
  };
};

const deleteLogParser = (parser) => {
  const parserName = parser.displayName ? `${parser.displayName} (${parser.name})` : parser.name;
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_LOG_PARSER,
      promise: api.deleteLogParser(parser.name),
      meta: {
        onSuccess() {
          success('configure.logsParser.modals.deleteParser.success', { parserName });
        },
        onFailure() {
          failure('configure.logsParser.modals.deleteParser.failure', { parserName });
        }
      }
    });
  };
};

const _fetchRuleFormats = () => {
  return {
    type: ACTION_TYPES.FETCH_FORMATS,
    promise: api.fetchRuleFormats()
  };
};

const fetchLogParsers = () => {
  return (dispatch) => {
    return dispatch({
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

const fetchDeviceTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_DEVICE_TYPES,
    promise: api.fetchDeviceTypes()
  };
};

const fetchDeviceClasses = () => {
  return {
    type: ACTION_TYPES.FETCH_DEVICE_CLASSES,
    promise: api.fetchDeviceClasses()
  };
};

const fetchMetas = () => {
  return {
    type: ACTION_TYPES.FETCH_METAS,
    promise: api.fetchMetas()
  };
};

const initializeLogParserRules = () => {
  return (dispatch) => {
    dispatch(_fetchRuleFormats());
    dispatch(fetchLogParsers());
    dispatch(fetchDeviceTypes());
    dispatch(fetchDeviceClasses());
    dispatch(fetchMetas());
  };
};

const fetchParserRules = () => {
  return (dispatch, getState) => {
    const logParserName = selectedLogParserName(getState());
    dispatch({
      type: ACTION_TYPES.FETCH_PARSER_RULES,
      promise: api.fetchParserRules(logParserName),
      meta: {
        onSuccess() {
          dispatch(highlightSampleLogs());
        }
      }
    });
  };
};

const selectParserRule = (index) => {
  return {
    type: ACTION_TYPES.SELECT_PARSER_RULE,
    payload: index
  };
};

const selectLogParser = (index) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SELECT_LOG_PARSER,
      payload: index
    });
    dispatch(fetchParserRules());
  };
};

const deleteParserRule = () => {
  return (dispatch, getState) => {
    const logParserName = selectedLogParserName(getState());
    const filteredRule = filterDeletedRule(getState());
    dispatch({
      type: ACTION_TYPES.DELETE_PARSER_RULE,
      promise: api.deleteParserRule(logParserName, filteredRule)
    });
  };
};

const addNewParserRule = (name) => {
  return {
    type: ACTION_TYPES.ADD_NEW_PARSER_RULE,
    payload: name
  };
};

const _deployLogParser = (logParserName) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DEPLOY_LOG_PARSER,
      promise: api.deployLogParser(logParserName),
      meta: {
        onSuccess() {
          const status = arguments.length > 1 && arguments[0] !== undefined ? arguments[0].data : {};
          if (status === 'COMPLETE') {
            success('configure.logsParser.modals.deployLogParser.success', { logParser: logParserName });
          } else if (status === 'PARTIAL') {
            failure('configure.logsParser.modals.deployLogParser.partialSuccess', { logParser: logParserName });
          } else {
            failure('configure.logsParser.modals.deployLogParser.failure', { logParser: logParserName });
          }
        },
        onFailure() {
          failure('configure.logsParser.modals.deployLogParser.apiError', { logParser: logParserName });
        }
      }
    });
  };
};

const deployLogParser = () => {
  return (dispatch, getState) => {
    const logParserName = selectedLogParserName(getState());
    dispatch(_deployLogParser(logParserName));
  };
};

const saveParserRule = () => {
  return (dispatch, getState) => {
    const logParserName = selectedLogParserName(getState());
    const rules = parserRules(getState());
    dispatch({
      type: ACTION_TYPES.SAVE_PARSER_RULE,
      promise: api.saveParserRule(logParserName, rules),
      meta: {
        onSuccess() {
          dispatch(fetchParserRules());
        }
      }
    });
  };
};

const updateSelectedRule = (rule) => {
  return {
    type: ACTION_TYPES.UPDATE_SELECTED_PARSER_RULE,
    payload: rule
  };
};

const highlightSampleLogs = (logText) => {
  return (dispatch, getState) => {
    const logs = logText || sampleLogs(getState());
    const rules = validRules(getState()); // only provide the valid rules to the highlighting call
    dispatch({
      type: ACTION_TYPES.HIGHLIGHT_SAMPLE_LOGS,
      promise: api.highlightSampleLogs({ logs: [logs] }, rules)
    });
  };
};

const discardRuleChanges = () => ({
  type: ACTION_TYPES.DISCARD_RULE_CHANGES
});

export {
  addLogParser,
  addNewParserRule,
  deleteLogParser,
  deleteParserRule,
  deployLogParser,
  discardRuleChanges,
  highlightSampleLogs,
  initializeLogParserRules,
  selectLogParser,
  selectParserRule,
  fetchParserRules,
  saveParserRule,
  fetchLogParsers,
  fetchDeviceTypes,
  fetchDeviceClasses,
  updateSelectedRule
};