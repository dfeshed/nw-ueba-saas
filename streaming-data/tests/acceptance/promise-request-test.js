import { later } from '@ember/runloop';
import RSVP from 'rsvp';
import { module, test } from 'qunit';
import { visit } from '@ember/test-helpers';
import { setupApplicationTest } from 'ember-qunit';

const { Promise } = RSVP;

module('Acceptance | Request | promiseRequest', function(hooks) {
  setupApplicationTest(hooks);

  test('socket make request and receive data', async function(assert) {
    await visit('/');
    const request = this.owner.lookup('service:request');

    const response = await request.promiseRequest({
      method: 'promise/_1',
      modelName: 'test',
      query: {}
    });

    assert.ok(true, 'Socket response received');
    assert.ok(response.data.length === 5, 'Socket response ');
  });

  /*
  * This test will ensure that you can stop a promise stream.
  * The server will not return a response for 1000 milliseconds.
  * At 500 milliseconds, this will stop the stream.
  * Any calls to resolve/reject promise will break test.
  */
  test('function is returned that can stop stream', async function(assert) {
    assert.expect(1);

    await visit('/');
    const request = this.owner.lookup('service:request');

    await new Promise(function(resolve) {
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

      later(() => {
        resolve();
      }, 600);
    });
  });

  test('when requireRequestId set to true, requestId is sent', async function(assert) {
    await visit('/');
    const request = this.owner.lookup('service:request');

    const response = await request.promiseRequest({
      method: 'promise/_5',
      modelName: 'test',
      query: {},
      streamOptions: {
        requireRequestId: true
      }
    });

    assert.ok(response.request.id !== undefined, 'returned request should contain request.id');
  });

  test('when requireRequestId set to false, no requestId sent', async function(assert) {
    await visit('/');
    const request = this.owner.lookup('service:request');

    const response = await request.promiseRequest({
      method: 'promise/_5',
      modelName: 'test',
      query: {},
      streamOptions: {
        requireRequestId: false
      }
    });

    assert.ok(response.request.id === undefined, 'returned request should not contain request.id');
  });

  test('when error code is returned, reject is called', async function(assert) {
    assert.expect(2);

    await visit('/');
    const request = this.owner.lookup('service:request');

    try {
      await request.promiseRequest({
        method: 'promise/_4',
        modelName: 'test',
        query: {}
      });
      assert.ok(false, 'promise should reject, not resolve');
    } catch (err) {
      assert.ok(true, 'Socket response should error out');
      assert.ok(err.code === 456, 'Response should contain code from server');
    }
  });

  /*
   * This test will ensure that if requireRequestId is set to false
   * and the server does not return a requestId, that the data still
   * makes it back to the resolution
   */
  test('can allow requestId to be set to false and still receive data', async function(assert) {
    await visit('/');
    const request = this.owner.lookup('service:request');

    const response = await request.promiseRequest({
      method: 'promise/_3',
      modelName: 'test',
      query: {},
      streamOptions: {
        requireRequestId: false
      }
    });

    assert.ok(true, 'Socket response received');
    assert.ok(response.data.length === 5, 'Socket response ');
  });
});