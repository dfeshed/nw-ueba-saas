import { lookup } from 'ember-dependency-lookup';

function fetchData(query, modelName, requireRequestId, dispatchPage, dispatchError) {
  const request = lookup('service:request');
  request.streamRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions: { requireRequestId },
    onResponse: dispatchPage,
    onError: dispatchError
  });
}

export default fetchData;