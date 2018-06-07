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

function findAllLogParsers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAllLogParsers',
    modelName: 'log-parser-rules',
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

function deleteParserRule(selectedLogParserName, filterDeletedRule) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deleteParserRule',
    modelName: 'log-parser-rules',
    query: {
      logDeviceParserName: selectedLogParserName,
      action: 'DELETE_RULE',
      parserRules: filterDeletedRule
    }
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

export default {
  addLogParser,
  deleteParserRule,
  deployLogParser,
  saveParserRule,
  fetchDeviceClasses,
  fetchDeviceTypes,
  fetchRuleFormats,
  fetchParserRules,
  findAllLogParsers
};
