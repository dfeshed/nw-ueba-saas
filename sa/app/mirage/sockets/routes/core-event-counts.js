export default function(server) {

  // Mock the response for store.stream('core-event-count') with the total size of 'core-events' db collection.
  server.route('core-event-count', 'stream', function(message, frames, server) {
    let firstFrame = (frames && frames[0]) || {};
    server.sendFrame('MESSAGE', {
      subscription: (firstFrame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: server.mirageServer.db['core-events'].length,
      request: firstFrame.body
    });
  });
}
