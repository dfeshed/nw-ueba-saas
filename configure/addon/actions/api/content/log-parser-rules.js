import { lookup } from 'ember-dependency-lookup';

function findAllLogParsers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAllLogParsers',
    modelName: 'log-parser-rules',
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
  deleteParserRule,
  deployLogParser,
  saveParserRule,
  fetchRuleFormats,
  fetchParserRules,
  findAllLogParsers
};
