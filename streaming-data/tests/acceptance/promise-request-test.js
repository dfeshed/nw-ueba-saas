import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';

const { run: { later } } = Ember;

moduleForAcceptance('Acceptance | Request | promiseRequest', {});

/*
 * This is a simple "will it return data" test
 */
test('socket make request and receive data', function(assert) {
  const done = assert.async();
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.promiseRequest({
      method: 'promise/_1',
      modelName: 'test',
      query: {}
    }).then(function(response) {
      assert.ok(true, 'Socket response received');
      assert.ok(response.data.length === 5, 'Socket response ');
      done();
    }).catch(function(/* response */) {
      assert.ok(false, 'Socket response errored out');
      done();
    });
  });
});

/*
 * This test will ensure that you can stop a promise stream.
 * The server will not return a response for 1000 milliseconds.
 * At 500 milliseconds, this will stop the stream.
 * Any calls to resolve/reject promise will break test.
 */
test('function is returned that can stop stream', function(assert) {
  assert.expect(1);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.promiseRequest({
      method: 'promise/_2',
      modelName: 'test',
      query: {},
      onInit(stopStreaming) {
        later(this, function() {
          stopStreaming();
          assert.ok(true, 'Stopped streaming before message came in');
        }, 500);
      }
    }).then(function(/* response */) {
      assert.ok(false, 'Socket response should not be received');
    }).catch(function(/* response */) {
      assert.ok(false, 'Socket response should not error out');
    });
  });
});

/*
 * This test will ensure that if requireRequestId is set to false
 * and the server does not return a requestId, that the data still
 * makes it back to the resolution
 */
test('can allow requestId to be set to false and still receive data', function(assert) {
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.promiseRequest({
      method: 'promise/_3',
      modelName: 'test',
      query: {},
      streamOptions: {
        requireRequestId: false
      }
    }).then(function(response) {
      assert.ok(true, 'Socket response received');
      assert.ok(response.data.length === 5, 'Socket response ');
    }).catch(function(/* response */) {
      assert.ok(false, 'Socket response should not error out');
    });
  });
});

test('when error code is returned, reject is called', function(assert) {
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.promiseRequest({
      method: 'promise/_4',
      modelName: 'test',
      query: {}
    }).then(function(/* response */) {
      assert.ok(false, 'promise should reject, not resolve');
    }).catch(function(response) {
      assert.ok(true, 'Socket response should not error out');
      assert.ok(response.code === 456, 'Response should contain code from server');
    });
  });
});

// From old acceptance test, see if way to test in new addon
//
// test('it will use the configuration value set in from-sockets.js file when no configuration override is found.', function(assert) {
//   visit('/');
//   andThen(function() {
//     return testStream({ requireRequestId: false }, function(response) {
//       assert.equal(response.request.stream.limit, 100000, 'The default stream limit should reflect the number set in the from-sockets.js file.');
//     });
//   });
// });