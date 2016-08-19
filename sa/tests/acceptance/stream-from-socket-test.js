import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import { Socket, Stream } from 'sa/services/data-access';
import config from 'sa/config/environment';

const { RSVP } = Ember;

const testStream = function(streamOptions, tests) {
  return new RSVP.Promise(function(resolve) {
    const stream = Stream.create(streamOptions);
    const socketConfig = Socket._findSocketConfig('test', 'stream');
    return stream.fromSocket({
      fetchSocketClient: () => Socket.connect(socketConfig.socketUrl),
      socketConfig
    }).autoStart()
      .subscribe({
        onNext: (response) => {
          tests(response);
          stream.stop();
          resolve();
        }
      });
  });
};

moduleForAcceptance('Acceptance | stream from socket', {
  // For testing purposes, I've added a beforeEach to clear out the defaultStreamLimit
  // so as to not adversely affect other tests.
  beforeEach() {
    config.socketRoutes.test.stream.defaultStreamLimit = undefined;
  },

  // After each test, destroy the MockServer instances we've created (if any),
  // so that the next test will not throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('it notifies subscribers of a socket response from a mock server', function(assert) {
  visit('/');

  andThen(function() {
    return testStream({}, function(response) {
      assert.equal(typeof response, 'object', 'Subscriber was notified with a response of expected data type.');
    });
  });
});

test('it will auto-generate request ids when they are required', function(assert) {
  visit('/');

  andThen(function() {
    return testStream({ requireRequestId: true }, function(response) {
      assert.ok(response.request.id, 'Subscriber was notified with a response that had an auto-generated request id.');
    });
  });
});

test('it will notify subscribers with responses even when request ids are not required', function(assert) {
  visit('/');

  andThen(function() {
    return testStream({ requireRequestId: false }, function(response) {
      assert.equal(typeof response, 'object', 'Subscriber was notified with a response of expected data type.');
      assert.equal(response.request.id, undefined, 'Response did not include an auto-generated request id.');
    });
  });
});

test('it will use the configuration value set in the environment.js file.', function(assert) {
  let defaultStreamLimit = config.socketRoutes.test.stream.defaultStreamLimit = 5;

  visit('/');
  andThen(function() {
    return testStream({ requireRequestId: false }, function(response) {
      assert.equal(response.data.length, response.request.stream.limit, 'The stream chunk that was returned is equal to the limit.');
      assert.equal(response.data.length <= response.meta.total, true, 'The stream list is being limited by the default setting.');
      assert.equal(response.request.stream.limit, defaultStreamLimit, 'The default stream limit should reflect the number passed to the configuration file.');
    });
  });
});

test('it will use the configuration value set in from-sockets.js file when no configuration override is found.', function(assert) {
  visit('/');
  andThen(function() {
    return testStream({ requireRequestId: false }, function(response) {
      assert.equal(response.request.stream.limit, 100000, 'The default stream limit should reflect the number set in the from-sockets.js file.');
    });
  });
});

test('it does have a response that includes a request id.', function(assert) {
  visit('/');

  andThen(function() {
    return testStream({ requireRequestId: true }, function(response) {
      assert.equal(response.request.id.length > 0, true, 'Response does include an auto-generated request id.');
    });
  });
});
