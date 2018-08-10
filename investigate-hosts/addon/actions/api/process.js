import { lookup } from 'ember-dependency-lookup';

// Get processes based on agentId and scanTime. query = { agentId, scanTime }
const getProcessTree = (query) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getProcessTree',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

// Get processes based on sorted order. query = { agentId, scanTime }
const getProcessList = (query, { key, descending }) => {
  query.sort = { keys: [key], descending };
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getProcessList',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

// Get individual process details based on query{ agentId, scanTime, pid: processId }
const getProcess = (query) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getProcess',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

// Get process context for a particular processId
const getProcessFileContext = (processId) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'getHostFileContext',
    modelName: 'endpoint',
    query: { data: processId }
  });
};

export {
  getProcessList,
  getProcessTree,
  getProcess,
  getProcessFileContext
};
