import { module, test } from 'qunit';
import { later } from '@ember/runloop';
import { setupApplicationTest } from 'ember-qunit';
import { visit } from '@ember/test-helpers';

module('Acceptance | Request | streamRequest', function(hooks) {
  setupApplicationTest(hooks);

  test('socket make request and receive data', async function(assert) {
    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

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

  test('socket make request and receive multiple pages of data', async function(assert) {
    let callCount = 0;
    let allResponseData = [];

    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

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

  test('socket can cancel', async function(assert) {
    let callCount = 0;

    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

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

  test('will call onInit before stream starts', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

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

  test('will call onStopped when stream is stopped by client', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

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

  test('will call onError when server returns error', async function(assert) {
    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    return request.streamRequest({
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

  test('will call onCompleted when server sends an indication that it is done', async function(assert) {
    let callCount = 0;

    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

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
