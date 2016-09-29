const buildBaseQuery = function(endpointId, eventId) {
  const query = {
    filter: [{
      field: 'endpointId',
      value: endpointId
    }, {
      field: 'sessionId',
      value: eventId
    }]
  };

  return query;
};

const addStreaming = function(_query) {
  const query = {
    ..._query,
    page: {
      index: 0,
      size: 100
    },
    stream: {
      batch: 10,
      limit: 100000
    }
  };

  return query;
};

export {
  buildBaseQuery,
  addStreaming
};