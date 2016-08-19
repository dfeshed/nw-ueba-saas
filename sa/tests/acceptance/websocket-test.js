import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import { Socket } from 'sa/services/data-access';
import config from 'sa/config/environment';

const { RSVP } = Ember;

const TEST_CONFIG = config.socketRoutes.test;
const TEST_SOCKET_URL = TEST_CONFIG.socketUrl;
const TEST_STREAM_CONFIG = TEST_CONFIG.stream;

moduleForAcceptance('Acceptance | Socket', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('Socket can connect to a mock server', function(assert) {
  let client = null;

  assert.expect(2);

  visit('/');

  andThen(function() {
    return Socket.connect(TEST_SOCKET_URL)
      .then((serviceClient) => {
        assert.ok(!!serviceClient, `Socket could not get a connected client to the server at ${TEST_SOCKET_URL}.`);
        client = serviceClient;
      });
  });

  andThen(function() {
    return client.disconnect().then(() => {
      assert.ok(true, 'socket disconnect callback was not invoked.');
    });
  });
});

test('Socket can connect, subscribe to a topic, receive a response & disconnect from a mock server', function(assert) {
  let client = null;

  visit('/');

  andThen(function() {
    return Socket.connect(TEST_SOCKET_URL)
      .then((serviceClient) => {
        assert.ok(!!serviceClient, `Socket could not get a connected client to the server at ${TEST_SOCKET_URL}.`);
        client = serviceClient;
      });
  });

  andThen(function() {
    return new RSVP.Promise(function(resolve) {
      let sub = client.subscribe(TEST_STREAM_CONFIG.subscriptionDestination, function(message) {
        assert.ok(!!message, `Socket could not get a response from a subscription to ${TEST_STREAM_CONFIG.subscriptionDestination}.`);
        resolve();
      });
      sub.send({}, {}, TEST_STREAM_CONFIG.requestDestination);
    });
  });

  andThen(function() {
    return client.disconnect().then(() => {
      assert.ok(true, 'Socket disconnect callback was not invoked.');
    });
  });
});
