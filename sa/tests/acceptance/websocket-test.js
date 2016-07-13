import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import websocket from 'sa/websocket/service';
import config from 'sa/config/environment';

const TEST_CONFIG = config.socketRoutes.test;
const TEST_SOCKET_URL = TEST_CONFIG.socketUrl;
const TEST_STREAM_CONFIG = TEST_CONFIG.stream;

moduleForAcceptance('Acceptance | websocket', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('service can connect to a mock server', function(assert) {
  let client = null;

  assert.expect(3);

  visit('/');

  andThen(function() {
    const service = websocket.create({});
    assert.ok(service, 'Service could not be instantiated.');

    return service.connect(TEST_SOCKET_URL)
      .then((serviceClient) => {
        assert.ok(!!serviceClient, `Service could not get a connected client to the server at ${TEST_SOCKET_URL}.`);
        client = serviceClient;
      });
  });

  andThen(function() {
    return client.disconnect().then(() => {
      assert.ok(true, 'Service disconnect callback was not invoked.');
    });
  });
});

test('service can connect, subscribe to a topic, receive a response & disconnect from a mock server', function(assert) {
  let client = null;

  visit('/');

  andThen(function() {
    const service = websocket.create({});
    assert.ok(service, 'Service could not be instantiated.');

    return service.connect(TEST_SOCKET_URL)
      .then((serviceClient) => {
        assert.ok(!!serviceClient, `Service could not get a connected client to the server at ${TEST_SOCKET_URL}.`);
        client = serviceClient;
      });
  });

  andThen(function() {
    return new Ember.RSVP.Promise(function(resolve) {
      let sub = client.subscribe(TEST_STREAM_CONFIG.subscriptionDestination, function(message) {
        assert.ok(!!message, `Service could not get a response from a subscription to ${TEST_STREAM_CONFIG.subscriptionDestination}.`);
        resolve();
      });
      sub.send({}, {}, TEST_STREAM_CONFIG.requestDestination);
    });
  });

  andThen(function() {
    return client.disconnect().then(() => {
      assert.ok(true, 'Service disconnect callback was not invoked.');
    });
  });
});
