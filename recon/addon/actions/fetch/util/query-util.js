import { promiseRequest } from 'streaming-data/services/data-access/requests';

const _addFilter = (query, field, value, valueKey = 'value') => {
  if (!query.filter) {
    query.filter = [];
  }

  const obj = { field };
  obj[valueKey] = value;

  query.filter.push(obj);
  return query;
};

const endpointFilter = (endpointId) => {
  const query = {
    filter: [{
      field: 'endpointId',
      value: endpointId
    }]
  };

  return query;
};

const buildBaseQuery = (endpointId, eventId) => {
  const query = endpointFilter(endpointId);
  query.filter.push({
    field: 'sessionId',
    value: eventId
  });
  return query;
};

const addSessionQueryFilter = (query, sessionId) => {
  const sessionQueryString = `(sessionid = ${sessionId})`;
  if (!query.filter) {
    query.filter = [];
  }

  const queryFilter = query.filter.findBy('field', 'query');
  if (queryFilter) {
    queryFilter.value = [queryFilter.value, sessionQueryString].join(' && ');
  } else {
    query.filter.push({
      field: 'query',
      value: sessionQueryString
    });
  }

  return query;
};

const addCatchAllTimeRange = (query) => {
  return _addFilter(
    query,
    'timeRange',
    {
      from: 0,
      to: +new Date() / 1000
    },
    'range'
  );
};

const addStreaming = (_query) => {
  const query = {
    ..._query,
    page: {
      index: 0,
      size: 10000 // can't page yet in UI yet, so just setting high
    },
    stream: {
      batch: 10,
      limit: 100000
    }
  };

  return query;
};

const basicPromiseRequest = (endpointId, eventId, modelName) => {
  const query = buildBaseQuery(endpointId, eventId);
  return promiseRequest({
    method: 'query',
    modelName,
    query
  });
};

const addFileTypeFilter = (query, type) => {
  return _addFilter(
    query,
    'filetype',
    type
  );
};

const addFileSelectionsFilter = (query, filenames = []) => {
  if (filenames.length) {
    query = _addFilter(
      query,
      'fileSelections',
      filenames,
      'values'
    );
  }
  return query;
};

const addSessionIdsFilter = (query, ids) => {
  return _addFilter(
    query,
    'sessionIds',
    ids,
    'values'
  );
};

export {
  addCatchAllTimeRange,
  addFileTypeFilter,
  addFileSelectionsFilter,
  addSessionIdsFilter,
  endpointFilter,
  buildBaseQuery,
  addStreaming,
  addSessionQueryFilter,
  basicPromiseRequest
};