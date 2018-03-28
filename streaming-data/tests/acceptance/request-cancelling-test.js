import { module, test } from 'qunit';
import { later } from '@ember/runloop';
import { setupApplicationTest } from 'ember-qunit';
import { visit } from '@ember/test-helpers';

import RSVP from 'rsvp';
const { Promise } = RSVP;

module('Acceptance | Request | cancelPreviouslyExecuting', function(hooks) {
  setupApplicationTest(hooks);

  // promiseRequest
  // cancelPreviouslyExecuting: true
  // request success
  test(
    'when cancelPreviouslyExecuting is true if the same promiseRequest ' +
    'is executed twice (and the response is slow), and request succeeds, ' +
    'the second promise\'s resolve is only function called', async function(assert) {

    assert.expect(1);

    await visit('/');
    const request = this.owner.lookup('service:request');

    const requestConfig = {
      method: 'promise/_7',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: true
      }
    };

    await new Promise(async function(resolve) {
      later(() => {
        resolve();
      }, 500);

      try {
        await request.promiseRequest(requestConfig);
        assert.notOk(true, '1st promise should not resolve');
      } catch (err) {
        assert.notOk(true, '1st promise should not reject');
      }
    });

    try {
      await request.promiseRequest({ ...requestConfig });
      assert.ok(true, 'Should execute 2nd execution\'s resolve');
    } catch (err) {
      assert.notOk(true, 'Promise should not reject');
    }
  });

  // promiseRequest
  // cancelPreviouslyExecuting: true
  // request fail
  test(
    'when cancelPreviouslyExecuting is true if the same promiseRequest ' +
    'is executed twice (and the response is slow), and request fails, ' +
    'the second promise\'s reject is only function called', async function(assert) {

    assert.expect(1);

    await visit('/');
    const request = this.owner.lookup('service:request');

    const requestConfig = {
      method: 'promise/_8',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: true
      }
    };

    await new Promise(async function(resolve) {
      later(() => {
        resolve();
      }, 500);

      try {
        await request.promiseRequest(requestConfig);
        assert.notOk(true, '1st promise should not resolve');
      } catch (err) {
        assert.notOk(true, '1st promise should not reject');
      }
    });

    try {
      await request.promiseRequest({ ...requestConfig });
      assert.notOk(true, 'Call failed, should not resolve, should reject');
    } catch (err) {
      assert.ok(true, '2nd Promise should reject');
    }
  });

  // promiseRequest
  // cancelPreviouslyExecuting: false
  // request success
  test(
    'when cancelPreviouslyExecuting is false if the same promiseRequest ' +
    'is executed twice, and requests succeed, both promise\'s resolve functions ' +
    'are called', async function(assert) {
    assert.expect(2);

    await visit('/');
    const request = this.owner.lookup('service:request');

    const requestConfig = {
      method: 'promise/_9',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: false
      }
    };

    await new Promise(function(resolve) {
      later(() => {
        resolve();
      }, 500);

      try {
        request.promiseRequest(requestConfig);
        assert.ok(true, '1st promise should resolve');
      } catch (err) {
        assert.notOk(true, '1st promise should not reject');
      }
    });

    try {
      await request.promiseRequest({ ...requestConfig });
      assert.ok(true, '2nd promise should resolve');
    } catch (err) {
      assert.notOk(true, '2nd Promise should not reject');
    }
  });

  // promiseRequest
  // cancelPreviouslyExecuting: false
  // request fail
  test(
    'when cancelPreviouslyExecuting is false if the same promiseRequest ' +
    'is executed twice, and requests fail, both promise\'s catch functions ' +
    'are called', async function(assert) {
    assert.expect(2);

    await visit('/');
    const request = this.owner.lookup('service:request');

    const requestConfig = {
      method: 'promise/_10',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: false
      }
    };

    await new Promise(async function(resolve) {
      later(() => {
        resolve();
      }, 500);
      try {
        await request.promiseRequest(requestConfig);
        assert.notOk(true, '1st promise should not resolve');
      } catch (err) {
        assert.ok(true, '1st promise should reject');
      }
    });

    try {
      await request.promiseRequest({ ...requestConfig });
      assert.notOk(true, '2nd promise should not resolve');
    } catch (err) {
      assert.ok(true, '2nd promise should reject');
    }
  });

  // streamRequest
  // cancelPreviouslyExecuting: true
  // request success
  test(
    'when cancelPreviouslyExecuting is true if the same streamRequest ' +
    'is executed twice (and the response is slow), and request succeeds, ' +
    'the second response callback is only function called', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.streamRequest({
      method: 'stream/_7',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: true
      },
      onResponse() {
        assert.notOk(true, '1st request should not respond');
      },
      onError() {
        assert.notOk(true, '1st request should not error');
      }
    });

    later(function() {
      request.streamRequest({
        method: 'stream/_7',
        modelName: 'test',
        query: {},
        streamOptions: {
          cancelPreviouslyExecuting: true
        },
        onResponse() {
          assert.ok(true, '2nd request should respond');
          done();
        },
        onError() {
          assert.notOk(true, '2nd request should not error');
          done();
        }
      });
    }, 500);
  });

  // streamRequest
  // cancelPreviouslyExecuting: true
  // request fail
  test(
    'when cancelPreviouslyExecuting is true if the same streamRequest ' +
    'is executed twice (and the response is slow), and request fails, ' +
    'the second responses error callback is only function called', async function(assert) {
    assert.expect(1);
    const done = assert.async();

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.streamRequest({
      method: 'stream/_8',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: true
      },
      onResponse() {
        assert.notOk(true, '1st request should not respond');
      },
      onError() {
        assert.notOk(true, '1st request should not error');
      }
    });

    later(function() {
      request.streamRequest({
        method: 'stream/_8',
        modelName: 'test',
        query: {},
        streamOptions: {
          cancelPreviouslyExecuting: true
        },
        onResponse() {
          assert.notOk(true, '2nd request should not respond');
        },
        onError() {
          assert.ok(true, '2nd request should error');
          done();
        }
      });
    }, 500);
  });

  // streamRequest
  // cancelPreviouslyExecuting: false
  // request success
  test(
    'when cancelPreviouslyExecuting is false if the same streamRequest ' +
    'is executed twice (and the response is slow), and request succeeds, ' +
    'both response callbacks are called', async function(assert) {
    assert.expect(2);
    const done = assert.async(2);

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.streamRequest({
      method: 'stream/_9',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: false
      },
      onResponse() {
        assert.ok(true, '1st request should respond');
        done();
      },
      onError() {
        assert.notOk(true, '1st request should not error');
      }
    });

    later(function() {
      request.streamRequest({
        method: 'stream/_9',
        modelName: 'test',
        query: {},
        streamOptions: {
          cancelPreviouslyExecuting: false
        },
        onResponse() {
          assert.ok(true, '2nd request should respond');
          done();
        },
        onError() {
          assert.notOk(true, '2nd request should not error');
        }
      });
    }, 500);
  });

  // streamRequest
  // cancelPreviouslyExecuting: false
  // request fail
  test(
    'when cancelPreviouslyExecuting is false if the same streamRequest is ' +
    'executed twice (and the response is slow), and request fails, both ' +
    'error callbacks are called', async function(assert) {
    assert.expect(2);
    const done = assert.async(2);

    await visit('/');
    const request = this.owner.lookup('service:request');

    request.streamRequest({
      method: 'stream/_10',
      modelName: 'test',
      query: {},
      streamOptions: {
        cancelPreviouslyExecuting: false
      },
      onResponse() {
        assert.notOk(true, '1st request should not respond');
      },
      onError() {
        assert.ok(true, '1st request should fail');
        done();
      }
    });

    later(function() {
      request.streamRequest({
        method: 'stream/_10',
        modelName: 'test',
        query: {},
        streamOptions: {
          cancelPreviouslyExecuting: false
        },
        onResponse() {
          assert.notOk(true, '2nd request should not respond');
        },
        onError() {
          assert.ok(true, '2nd request should fail');
          done();
        }
      });
    }, 500);
  });
});