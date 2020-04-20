import { lookup } from 'ember-dependency-lookup';

function fetchNotifications(dispatchInit, dispatchPage, dispatchError) {
  const request = lookup('service:request');
  request.streamRequest({
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
