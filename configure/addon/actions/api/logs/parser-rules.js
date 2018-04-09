import { lookup } from 'ember-dependency-lookup';

function deleteRule(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'deleteRecord',
    modelName: 'parser-rules',
    query: {
      ruleId: id
    }
  });
}

function addRule(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'addRecord',
    modelName: 'parser-rules',
    query: {
      ruleId: id
    }
  });
}

function findAllLogParsers() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAllLogParsers',
    modelName: 'parser-rules',
    query: {}
  });
}

function getRules(id) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getRules',
    modelName: 'parser-rules',
    query: {
      ruleId: id
    }
  });
}

function getFormats() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getFormats',
    modelName: 'parser-rules',
    query: {}
  });
}

export default {
  deleteRule,
  addRule,
  getFormats,
  getRules,
  findAllLogParsers
};
