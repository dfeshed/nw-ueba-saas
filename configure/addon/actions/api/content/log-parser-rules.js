import { lookup } from 'ember-dependency-lookup';

/* function deleteParserRule(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deleteRecord',
    modelName: 'parser-rules',
    query: {
      ruleId: id
    }
  });
}

function addParserRule(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'addRecord',
    modelName: 'parser-rules',
    query: {
      ruleId: id
    }
  });
} */

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

export default {
  // deleteParserRule,
  // addParserRule,
  fetchRuleFormats,
  fetchParserRules,
  findAllLogParsers
};
