/**
 * @file MockServer message handlers that respond to requests used in automated testing.
 * Here we can register handlers for requests used in testing, and then the tests can confirm that a response was received.
 * @public
 */
export default function(server) {

  server.route('test', 'stream', function(message, frames, server) {
    // Respond with a list that contains a single dummy item.
    server.streamList(
      [
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' },
        { id: 'dummy' }
      ],
      null,
      null,
      frames);
  });

  server.route('test', 'query', function(message, frames, server) {
    // Respond with a list that contains a single dummy item.
    server.sendList(
      [{ id: 'dummy' }],
      null,
      null,
      frames);
  });

  server.route('test', 'findRecord', function(message, frames, server) {
    // Respond with a dummy record.
    let frame = (frames && frames[0]) || {};
    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: { id: 'dummy' },
      request: frame.body
    });
  });

  server.route('test', 'updateRecord', function(message, frames, server) {
    // Respond with dummy confirmation.
    let frame = (frames && frames[0]) || {};
    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: { id: 'dummy' },
      request: frame.body
    });
  });

}
