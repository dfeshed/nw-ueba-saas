import { module, test } from 'qunit';
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


  test('stream should not receive 2nd page of data', async function(assert) {
    let callCount = 0;

    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.pagedStreamRequest({
      method: 'paged-stream/_2', // stream/_2 will send many pages
      modelName: 'test',
      query: {},
      onResponse() {
        ++callCount;
      }
    });

    setTimeout(() => {
      assert.ok(callCount === 1, `Only one response should be received, got ${callCount}`);
      done();
    }, 5000);
  });

  test('pagedStreamRequest returns cursor object', async function(assert) {
    assert.expect(4);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse() {
        assert.ok(typeof cursor.first === 'function', 'cursor.first is a function');
        assert.ok(typeof cursor.previous === 'function', 'cursor.previous is a function');
        assert.ok(typeof cursor.next === 'function', 'cursor.next is a function');
        assert.ok(typeof cursor.last === 'function', 'cursor.last is a function');
        done();
      }
    });
  });

  test('will call onCompleted when server sends an indication that it is done', async function(assert) {
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

  test('Cursor.next() gets next page', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response) {
        responseCount++;
        if (responseCount === 1) {
          cursor.next();
        } else {
          assert.deepEqual(response.data, [4, 5, 6], 'onCompleted was not called');
          done();
        }
      }
    });

  });

  test('Cursor.previous() gets previous page', async function(assert) {
    assert.expect(3);
    const done = assert.async();

    const markers = [];
    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response) {
        responseCount++;
        markers.push(response.meta.marker);
        if (responseCount === 1 || responseCount === 2) {
          cursor.next();
          return;
        }

        if (responseCount === 3) {
          cursor.previous();
          return;
        }

        assert.ok(responseCount === 4, 'response called 4 times');
        assert.deepEqual(response.data, [4, 5, 6], 'previous returned 2nd page of results');
        assert.deepEqual(markers, ['abc', 'def', 'ghi', 'def'], 'the correct markers have been returned');

        done();
      }
    });

  });

  test('Cursor.first() gets first page', async function(assert) {
    assert.expect(3);
    const done = assert.async();

    const markers = [];
    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response) {
        responseCount++;
        markers.push(response.meta.marker);

        // get a few pages in before calling first
        if (responseCount < 4) {
          cursor.next();
          return;
        }

        if (responseCount === 4) {
          cursor.first();
          return;
        }

        assert.ok(responseCount === 5, 'response called 5 times');
        assert.deepEqual(response.data, [1, 2, 3], 'first returned 1st page of results');
        assert.deepEqual(markers, ['abc', 'def', 'ghi', 'jkl', 'abc'], 'the correct markers have been returned');

        done();

      }
    });

  });

  test('Cursor.last() will error out if called and last page has not been encountered', async function(assert) {
    assert.expect(2);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response) {
        assert.ok(response.meta.complete === false, 'the response is not yet completed');
        try {
          cursor.last();
          assert.ok(false, 'cursor.last should throw exception');
        } catch (err) {
          assert.equal(err.message, 'Last page has not yet been encountered, you cannot call Cursor.last()', 'error should have correct message');
          done();
        }
      }
    });
  });

  test('when last page has been encountered Cursor.last() gets it', async function(assert) {
    assert.expect(3);
    const done = assert.async();

    const markers = [];
    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response) {
        responseCount++;
        markers.push(response.meta.marker);

        // get a few pages in before calling first
        if (responseCount < 9) {
          cursor.next();
          return;
        }

        if (responseCount === 9) {
          cursor.first();
          return;
        }

        if (responseCount === 10) {
          cursor.last();
          return;
        }

        assert.ok(responseCount === 11, 'response called 11 times');
        assert.deepEqual(response.data, [25, 26], 'first returned 1st page of results');
        assert.deepEqual(markers, ['abc', 'def', 'ghi', 'jkl', 'mno', 'pqr', 'stu', 'vwx', undefined, 'abc', undefined], 'the correct markers have been returned');

        done();

      }
    });
  });

  test('canFirst, canPrevious, canNext, canLast flags all appropraitely set', async function(assert) {
    assert.expect(54);
    const done = assert.async();

    const markers = [];
    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse(response) {
        responseCount++;
        markers.push(response.meta.marker);

        if (responseCount === 1) {
          assert.ok(cursor.canFirst === false, 'cannot go to first page because on it');
          assert.ok(cursor.canPrevious === false, 'cannot go to previous page because on first page');
          assert.ok(cursor.canNext === true, 'can go to next page because there are more');
          assert.ok(cursor.canLast === false, 'cannot go to last page because it has not been encountered');
          cursor.next();
          return;
        }

        // get a few pages in before calling first
        if (responseCount < 9) {
          assert.ok(cursor.canFirst === true, 'can go to first page because not on it');
          assert.ok(cursor.canPrevious === true, 'can go to previous page there is one');
          assert.ok(cursor.canNext === true, 'can go to next page because there are more');
          assert.ok(cursor.canLast === false, 'cannot go to last page because it has not been encountered');
          cursor.next();
          return;
        }

        if (responseCount === 9) {
          assert.ok(cursor.canFirst === true, 'can go to first page because not on it');
          assert.ok(cursor.canPrevious === true, 'can go to previous page there is one');
          assert.ok(cursor.canNext === false, 'cannot go to next page because on last page');
          assert.ok(cursor.canLast === false, 'cannot go to last page because on last page');
          cursor.first();
          return;
        }

        if (responseCount === 10) {
          assert.ok(cursor.canFirst === false, 'cannot go to first page because on it');
          assert.ok(cursor.canPrevious === false, 'cannot go to previous page because on first page');
          assert.ok(cursor.canNext === true, 'can go to next page because on first');
          assert.ok(cursor.canLast === true, 'can go to last page because it has been encountered and not on it');
          cursor.next();
          return;
        }

        if (responseCount === 11 || responseCount === 12 || responseCount === 14) {
          assert.ok(cursor.canFirst === true, 'can go to first page because not on it');
          assert.ok(cursor.canPrevious === true, 'can go to previous page there is one');
          assert.ok(cursor.canNext === true, 'can go to next page because there are more');
          assert.ok(cursor.canLast === true, 'cannot go to last page because it has been encountered');
          cursor.next();
          return;
        }

        if (responseCount === 13) {
          cursor.previous();
          return;
        }

        assert.deepEqual(response.data, [10, 11, 12], 'first returned 3rd page of results');
        assert.deepEqual(markers, ['abc', 'def', 'ghi', 'jkl', 'mno', 'pqr', 'stu', 'vwx', undefined, 'abc', 'def', 'ghi', 'jkl', 'ghi', 'jkl'], 'the correct markers have been returned');

        done();
      }
    });
  });

  test('Cursor.first() will noop if called and on first page', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse() {
        responseCount++;
        // call first straight away, should do nothing
        if (responseCount === 1) {
          cursor.first();
        }
      }
    });

    // give it time to call onresponse again
    setTimeout(() => {
      assert.ok(responseCount === 1, 'onResponse only called once');
      done();
    }, 1000);

  });

  test('Cursor.previous() will noop if called and on first page', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse() {
        responseCount++;
        // call first straight away, should do nothing
        if (responseCount === 1) {
          cursor.previous();
        }
      }
    });

    // give it time to call onresponse again
    setTimeout(() => {
      assert.ok(responseCount === 1, 'onResponse only called once');
      done();
    }, 1000);

  });

  // To be coded

  test('Cursor.next() will noop if called and on last page', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse() {
        responseCount++;
        // call first straight away, should do nothing
        if (responseCount < 9) {
          cursor.next();
          return;
        }

        if (responseCount === 9) {
          // give it time to call onresponse again
          setTimeout(() => {
            assert.ok(responseCount === 9, 'onResponse not called 10th time');
            done();
          }, 1000);

          // try calling again?
          cursor.next();
        }
      }
    });
  });

  test('Cursor.next() will noop if called and on last page', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    let responseCount = 0;

    await visit('/');
    const request = this.owner.lookup('service:request');

    const cursor = request.pagedStreamRequest({
      method: 'paged-stream/_2',
      modelName: 'test',
      query: {},
      onResponse() {
        responseCount++;
        // call first straight away, should do nothing
        if (responseCount < 9) {
          cursor.next();
          return;
        }

        if (responseCount === 9) {
          // give it time to call onresponse again
          setTimeout(() => {
            assert.ok(responseCount === 9, 'onResponse not called 10th time');
            done();
          }, 1000);

          // try calling again?
          cursor.last();
        }
      }
    });
  });

});
