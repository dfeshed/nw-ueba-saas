import { streamRequest } from 'streaming-data/services/data-access/requests';

function fetchData(query, modelName, dispatchPage, dispatchError) {
  streamRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions: { requireRequestId: false },
    onResponse: dispatchPage,
    onError: dispatchError
  });
}

export default fetchData;