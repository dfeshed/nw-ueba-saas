import { lookup } from 'ember-dependency-lookup';

function addLogParser(parser) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'update',
    modelName: 'log-parser-rules',
    query: {
      ...parser,
      action: 'ADD_PARSER'
    }
  });
}

function deleteLogParser(logDeviceParserName) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'update',
    modelName: 'log-parser-rules',
    query: {
      logDeviceParserName,
      action: 'DELETE_PARSER'
    }
  });
}

function findAllLogParsers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAllLogParsers',
    modelName: 'log-parser-rules',
    query: {}
  });
}

function fetchMetas() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'rule-metas',
    query: {}
  });
}

function fetchDeviceTypes() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'device-types',
    query: {}
  });
}

function fetchDeviceClasses() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'device-classes',
    query: {}
  });
}

function fetchParserRules(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'fetchParserRules',
    modelName: 'log-parser-rules',
    query: {
      filter: [{
        field: 'name',
        value: id }],
      sort: [],
      stream: {},
      id: ''
    }
  });
}

function fetchRuleFormats() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'fetchRuleFormats',
    modelName: 'log-parser-rules',
    query: {}
  });
}

function deployLogParser(selectedLogParserName) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deployLogParser',
    modelName: 'log-parser-rules',
    query: {
      data: selectedLogParserName,
      action: 'DEPLOY_LOG_PARSER'
    }
  });
}

function saveParserRule(selectedLogParserName, rules) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'saveParserRule',
    modelName: 'log-parser-rules',
    query: {
      logDeviceParserName: selectedLogParserName,
      action: 'EDIT_RULE',
      parserRules: rules
    }
  });
}

function highlightSampleLogs(logs, parserRules) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'highlight',
    modelName: 'log-parser-rules',
    query: {
      logs,
      parserRules
    }
  });
}

export default {
  addLogParser,
  deleteLogParser,
  deployLogParser,
  saveParserRule,
  fetchDeviceClasses,
  fetchDeviceTypes,
  fetchRuleFormats,
  fetchMetas,
  fetchParserRules,
  findAllLogParsers,
  highlightSampleLogs
};
