import { promiseRequest } from 'streaming-data/services/data-access/requests';

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

  let queryFilter = query.filter.findBy('field', 'query');
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
  if (!query.filter) {
    query.filter = [];
  }

  query.filter.push({
    field: 'timeRange',
    range: {
      from: 0,
      to: +new Date() / 1000
    }
  });

  return query;
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

export {
  addCatchAllTimeRange,
  endpointFilter,
  buildBaseQuery,
  addStreaming,
  addSessionQueryFilter,
  basicPromiseRequest
};