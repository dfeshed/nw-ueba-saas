import { lookup } from 'ember-dependency-lookup';

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

const addStreaming = (_query, pageSize = 10000, batchSize = 10, limit = 100000, rowIndex = 0) => {
  // can't page yet in UI yet, so just set default pageSize high
  const query = {
    ..._query,
    page: {
      index: rowIndex,
      size: pageSize
    },
    stream: {
      batch: batchSize,
      limit
    }
  };

  return query;
};

const addMaxPackets = (_query, maxPackets = 2500) => {
  return _addFilter(_query, 'maxPackets', maxPackets);
};

const basicPromiseRequest = (endpointId, eventId, modelName, streamOptions = {}) => {
  const query = buildBaseQuery(endpointId, eventId);
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'query',
    modelName,
    query,
    streamOptions
  });
};

const addFileTypeFilter = (query, type) => {
  return _addFilter(
    query,
    'filetype',
    type
  );
};

const addEventTypeFilter = (query, type) => {
  return _addFilter(
    query,
    'eventtype',
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

const addDecode = (query, decode) => {
  return _addFilter(query, 'decode', decode);
};

const addFilenameFilter = (query, filename) => {
  if (filename) {
    query = _addFilter(
      query,
      'filename',
      filename
    );
  }
  return query;
};

export {
  addCatchAllTimeRange,
  addFileTypeFilter,
  addFileSelectionsFilter,
  addFilenameFilter,
  addEventTypeFilter,
  addMaxPackets,
  addSessionIdsFilter,
  endpointFilter,
  buildBaseQuery,
  addStreaming,
  addSessionQueryFilter,
  basicPromiseRequest,
  addDecode
};
