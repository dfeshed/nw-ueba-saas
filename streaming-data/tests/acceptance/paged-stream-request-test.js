import { module, test, skip } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { visit } from '@ember/test-helpers';

module('Acceptance | Request | pagedStreamRequest', function(hooks) {
  setupApplicationTest(hooks);

  test('socket make request and receive data', async function(assert) {
    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.pagedStreamRequest({
      method: 'stream/_1',
      modelName: 'test',
      query: {},
      onResponse(response) {
        assert.ok(true, 'Socket response received');
        assert.ok(response.data.length === 5, 'Socket response ');
        done();
      }
    });
  });

  test('will call onInit before stream starts', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.pagedStreamRequest({
      method: 'stream/_1',  // reusing stream/_1 just to verify onInit called
      modelName: 'test',
      query: {},
      onResponse() {
        done();
      },
      onInit() {
        assert.ok(true, 'More messages came in and they should not have');
      }
    });
  });

  test('will call onError when server returns error', async function(assert) {
    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    return request.pagedStreamRequest({
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

  test('error message logged if no error handler provided', async function(assert) {
    const done = assert.async();
    assert.expect(1);

    /* eslint-disable no-console */
    const oldConsoleWarn = console.warn;

    console.warn = function() {
      assert.ok(true, 'warning was issued');
      console.warn = oldConsoleWarn;
      done();
    };
    /* eslint-enable no-console */

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.pagedStreamRequest({
      method: 'stream/_11',
      modelName: 'test',
      query: {},
      onResponse() {}
    });
  });

  //
  // Needs to be re-coded for pagedStreamRequest
  //

  skip('will call onCompleted when server sends an indication that it is done', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.pagedStreamRequest({
      method: 'paged-stream/_1',
      modelName: 'test',
      query: {},
      onResponse() {},
      onCompleted() {
        assert.ok(true, 'onCompleted was not called');
        done();
      }
    });
  });

  skip('stream should not receive 2nd page of data', async function(assert) {
    let callCount = 0;

    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.pagedStreamRequest({
      method: 'stream/_2', // stream/_2 will send many pages
      modelName: 'test',
      query: {},
      onResponse() {
        ++callCount;
      }
    });

    setTimeout(() => {
      assert.ok(callCount === 1, `Only one response should be received, got ${callCount}`);
      done();
    }, 4000);
  });

  // To be coded

  skip('pagedStreamRequest returns cursor object');

  skip('Cursor.first() gets first page if not on it');

  skip('when has previous page Cursor.previous() gets it');

  skip('when has next page Cursor.next() gets it');

  skip('when last page has been encountered Cursor.last() gets it');

  skip('canFirst, canPrevious, canNext, canLast flags all appropraitely set');

  skip('first() will noop if called and on first page');

  skip('previous() will noop if called and on first page');

  skip('next() will noop if called and on last page');

  skip('last() will noop if called and on last page');

  skip('last() will error out if called and last page has not been encountered');

});
