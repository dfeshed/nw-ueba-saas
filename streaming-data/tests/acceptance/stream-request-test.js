import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';

const { run: { later } } = Ember;

moduleForAcceptance('Acceptance | Request | streamRequest', {});

/*
 * This is a simple "will it return data" test
 */
test('socket make request and receive data', function(assert) {
  const done = assert.async();
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_1',
      modelName: 'test',
      query: {},
      onResponse(response, stopStreaming) {
        assert.ok(true, 'Socket response received');
        assert.ok(response.data.length === 5, 'Socket response ');
        stopStreaming();
        done();
      }
    });
  });
});

/*
 * Basic 'can get multiplte pages back' test
 */
test('socket make request and receive multiple pages of data', function(assert) {
  let callCount = 0;
  let allResponseData = [];
  const done = assert.async();
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response, stopStreaming) {
        allResponseData = allResponseData.concat(response.data);
        if (++callCount === 2) {
          assert.ok(true, 'Multiple socket responses received');
          assert.ok(allResponseData.length === 10, 'Socket response had 10 things in it, 5 from each response');
          stopStreaming();
          done();
        }
      }
    });
  });
});

/*
 * Will not have response called after telling stream to stop, can successfully stop stream half way
 */
test('socket can cancel', function(assert) {
  let callCount = 0;
  const done = assert.async();
  assert.expect(1);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_3',
      modelName: 'test',
      query: {},
      onResponse(response, stopStreaming) {
        if (++callCount === 2) {
          later(this, function() {
            assert.ok(callCount === 2, 'More messages came in and they should not have');
            done();
          }, 2000);
          stopStreaming();
        }
      }
    });
  });
});

test('will call onInit before stream starts', function(assert) {
  const done = assert.async();
  assert.expect(1);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_1',  // reusing stream/_1 just to verify onInit called
      modelName: 'test',
      query: {},
      onResponse() {},
      onInit(stopStreaming) {
        stopStreaming();
        assert.ok(true, 'More messages came in and they should not have');
        done();
      }
    });
  });
});

test('will call onStopped when stream is stopped by client', function(assert) {
  const done = assert.async();
  assert.expect(1);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_2',  // reusing stream/_2 just to verify onStopped called
      modelName: 'test',
      query: {},
      onResponse(response, stopStreaming) {
        stopStreaming();
      },
      onStopped() {
        assert.ok(true, 'onStopped was not called');
        done();
      }
    });
  });
});

test('will call onError when server returns error', function(assert) {
  const done = assert.async();
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_4',
      modelName: 'test',
      query: {},
      onResponse() {},
      onError({ code }) {
        assert.ok(true, 'On error was not called');
        assert.ok(code === 100, 'Error code 100 was not returned');
        done();
      }
    });
  });
});

test('will call onCompleted when server sends an indication that it is done', function(assert) {
  let callCount = 0;
  const done = assert.async();
  assert.expect(2);
  visit('/');

  const request = this.application.__container__.lookup('service:request');

  andThen(function() {
    request.streamRequest({
      method: 'stream/_5',
      modelName: 'test',
      query: {},
      onResponse() {
        callCount++;
      },
      onCompleted() {
        assert.ok(true, 'onCompleted was not called');
        assert.ok(callCount === 4, 'onResponse was not called correct number of times before completed was called');
        done();
      }
    });
  });
});