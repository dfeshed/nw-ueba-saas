import { promiseRequest } from 'streaming-data/services/data-access/requests';

// Get processes based on agentId and scanTime. query = { agentId, scanTime }
const getProcessTree = (query) => {
  return promiseRequest({
    method: 'getProcessTree',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

// Get processes based on sorted order. query = { agentId, scanTime }
const getProcessList = (query, sort) => {
  query.sort = sort;
  return promiseRequest({
    method: 'getProcessList',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

// Get individual process details based on query{ agentId, scanTime, pid: processId }
const getProcess = (query) => {
  return promiseRequest({
    method: 'getProcess',
    modelName: 'endpoint',
    query: {
      data: query
    }
  });
};

// Get process context for a particular processId
const getProcessFileContext = (processId) => {
  return promiseRequest({
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