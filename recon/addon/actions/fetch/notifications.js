import { streamRequest } from 'streaming-data/services/data-access/requests';

function fetchNotifications(dispatchInit, dispatchPage, dispatchError) {
  streamRequest({
    method: 'stream',
    modelName: 'investigate-notification',
    query: {},
    streamOptions: { requireRequestId: false },
    onInit: dispatchInit,
    onResponse: dispatchPage,
    onError: dispatchError
  });
}

export default fetchNotifications;
