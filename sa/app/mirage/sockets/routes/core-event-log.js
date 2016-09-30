import { faker } from 'ember-cli-mirage';

export default function(server) {

  // Mock the response for store.stream('core-event-log'):
  server.route('core-event-log', 'stream', function(message, frames, server) {
    let [ firstFrame ] = frames;
    let { body: { filter } } = firstFrame;

    // Find the list of session ids in the request's filter.
    let sessionIdFilter = (filter || []).findBy('field', 'sessionIds');
    let sessionIds = sessionIdFilter && sessionIdFilter.values;

    // For each requested session id..
    (sessionIds || []).forEach((sessionId) => {

      // ..send a separate frame with the log data for just that session id.
      server.sendFrame('MESSAGE', {
        subscription: (firstFrame.headers || {}).id || '',
        'content-type': 'application/json'
      }, {
        code: 0,
        data: {
          sessionId,
          log: faker.lorem.words(1 + faker.random.number(50)).join(' ')
        },
        request: firstFrame.body
      },
      // random delay
      faker.random.number(2000));
    });
  });
}
