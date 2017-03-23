import { streamRequest } from 'streaming-data/services/data-access/requests';

function fetchData(query, modelName, requireRequestId, dispatchPage, dispatchError) {
  streamRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions: { requireRequestId },
    onResponse: dispatchPage,
    onError: dispatchError
  });
}

export default fetchData;