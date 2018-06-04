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
  saveParserRule,
  fetchRuleFormats,
  fetchParserRules,
  findAllLogParsers
};
