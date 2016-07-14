import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import websocket from 'sa/websocket/service';
import Stream from 'sa/utils/stream/base';

const { RSVP } = Ember;

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
    return new RSVP.Promise(function(resolve) {

      let stream = Stream.create();
      stream.fromSocket({
        websocket: websocket.create({}),
        socketConfigType: { modelName: 'test', method: 'stream' }
      }).autoStart()
        .subscribe(function(response) {
          assert.equal(typeof response, 'object', 'Subscriber was notified with a response of expected data type.');
          stream.stop();
          resolve();
        });

    });
  });
});

test('it will auto-generate request ids when they are required', function(assert) {
  assert.expect(1);
  visit('/');

  andThen(function() {
    return new RSVP.Promise(function(resolve) {

      let stream = Stream.create({ requireRequestId: true });
      stream.fromSocket({
        websocket: websocket.create({}),
        socketConfigType: { modelName: 'test', method: 'stream' }
      }).autoStart()
        .subscribe(function(response) {
          assert.ok(response.request.id, 'Subscriber was notified with a response that had an auto-generated request id.');
          stream.stop();
          resolve();
        });

    });
  });
});

test('it will notify subscribers with responses even when request ids are not required', function(assert) {
  assert.expect(2);
  visit('/');

  andThen(function() {
    return new RSVP.Promise(function(resolve) {

      let stream = Stream.create({ requireRequestId: false });
      stream.fromSocket({
        websocket: websocket.create({}),
        socketConfigType: { modelName: 'test', method: 'stream' }
      }).autoStart()
        .subscribe(function(response) {
          assert.equal(typeof response, 'object', 'Subscriber was notified with a response of expected data type.');
          assert.equal(response.request.id, undefined, 'Response did not include an auto-generated request id.');
          stream.stop();
          resolve();
        });

    });
  });
});
