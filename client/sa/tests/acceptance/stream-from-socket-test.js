import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import websocket from 'sa/websocket/service';
import Stream from 'sa/utils/stream/base';

moduleForAcceptance('Acceptance | stream from socket', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('it notifies subscribers of a socket response from a mock server', function(assert) {
  assert.expect(1);
  visit('/');

  andThen(function() {
    return new Ember.RSVP.Promise(function(resolve) {

      Stream.create().fromSocket({
        websocket: websocket.create({}),
        socketConfigType: { modelName: 'test', method: 'stream' }
      }).autoStart()
        .subscribe(function(response) {
          assert.equal(typeof response, 'object', 'Subscriber was notified with a response of expected data type.');
          resolve();
        });

    });
  });
});
