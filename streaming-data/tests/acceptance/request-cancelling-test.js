import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import { later } from 'ember-runloop';
import RSVP from 'rsvp';

const { Promise } = RSVP;

moduleForAcceptance('Acceptance | Request | cancelPreviouslyExecuting', {});

// promiseRequest
// cancelPreviouslyExecuting: true
// request success
test('when cancelPreviouslyExecuting is true if the same promiseRequest is executed twice (and the response is slow), and request succeeds, the second promise\'s resolve is only function called', function(assert) {
  let request;
  assert.expect(1);
  const done = assert.async();

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  const requestConfig = {
    method: 'promise/_7',
    modelName: 'test',
    query: {},
    streamOptions: {
      cancelPreviouslyExecuting: true
    }
  };

  andThen(function() {
    return new Promise(function(resolve) {
      request.promiseRequest(requestConfig)
        .then(function() {
          assert.notOk(true, '1st promise should not resolve');
        })
        .catch(function() {
          assert.notOk(true, '1st promise should not reject');
        });

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request
        .promiseRequest({ ...requestConfig })
        .then(function() {
          assert.ok(true, 'Should execute 2nd execution\'s resolve');
        })
        .catch(function() {
          assert.notOk(true, 'Promise should not reject');
        }).finally(done);
    });
  });
});

// promiseRequest
// cancelPreviouslyExecuting: true
// request fail
test('when cancelPreviouslyExecuting is true if the same promiseRequest is executed twice (and the response is slow), and request fails, the second promise\'s reject is only function called', function(assert) {
  let request;
  assert.expect(1);
  const done = assert.async();

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  const requestConfig = {
    method: 'promise/_8',
    modelName: 'test',
    query: {},
    streamOptions: {
      cancelPreviouslyExecuting: true
    }
  };

  andThen(function() {
    return new Promise(function(resolve) {
      request
        .promiseRequest(requestConfig)
        .then(function() {
          assert.notOk(true, '1st promise should not resolve');
        })
        .catch(function() {
          assert.notOk(true, '1st promise should not reject');
        });

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request
        .promiseRequest({ ...requestConfig })
        .then(function() {
          assert.notOk(true, 'Call failed, should not resolve, should reject');
        })
        .catch(function() {
          assert.ok(true, '2nd Promise should reject');
        }).finally(done);
    });
  });
});

// promiseRequest
// cancelPreviouslyExecuting: false
// request success
test('when cancelPreviouslyExecuting is false if the same promiseRequest is executed twice, and requests succeed, both promise\'s resolve functions are called', function(assert) {
  let request;
  assert.expect(2);
  const done = assert.async();

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  const requestConfig = {
    method: 'promise/_9',
    modelName: 'test',
    query: {},
    streamOptions: {
      cancelPreviouslyExecuting: false
    }
  };

  andThen(function() {
    return new Promise(function(resolve) {
      request
        .promiseRequest(requestConfig)
        .then(function() {
          assert.ok(true, '1st promise should resolve');
        })
        .catch(function() {
          assert.notOk(true, '1st promise should not reject');
        });

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request
        .promiseRequest({ ...requestConfig })
        .then(function() {
          assert.ok(true, '2nd promise should resolve');
        })
        .catch(function() {
          assert.notOk(true, '2nd Promise should not reject');
        }).finally(done);
    });
  });
});

// promiseRequest
// cancelPreviouslyExecuting: false
// request fail
test('when cancelPreviouslyExecuting is false if the same promiseRequest is executed twice, and requests fail, both promise\'s catch functions are called', function(assert) {
  let request;
  assert.expect(2);
  const done = assert.async();

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  const requestConfig = {
    method: 'promise/_10',
    modelName: 'test',
    query: {},
    streamOptions: {
      cancelPreviouslyExecuting: false
    }
  };

  andThen(function() {
    return new Promise(function(resolve) {
      request
        .promiseRequest(requestConfig)
        .then(function() {
          assert.notOk(true, '1st promise should not resolve');
        })
        .catch(function() {
          assert.ok(true, '1st promise should reject');
        });

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request
        .promiseRequest({ ...requestConfig })
        .then(function() {
          assert.notOk(true, '2nd promise should not resolve');
        })
        .catch(function() {
          assert.ok(true, '2nd promise should reject');
        }).finally(done);
    });
  });
});

// streamRequest
// cancelPreviouslyExecuting: true
// request success
test('when cancelPreviouslyExecuting is true if the same streamRequest is executed twice (and the response is slow), and request succeeds, the second response callback is only function called', function(assert) {
  let request;
  assert.expect(1);
  const done = assert.async();

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  andThen(function() {
    return new Promise(function(resolve) {
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

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request.streamRequest({
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
    });
  });
});

// streamRequest
// cancelPreviouslyExecuting: true
// request fail
test('when cancelPreviouslyExecuting is true if the same streamRequest is executed twice (and the response is slow), and request fails, the second responses error callback is only function called', function(assert) {
  let request;
  assert.expect(1);
  const done = assert.async();

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  andThen(function() {
    return new Promise(function(resolve) {
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

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request.streamRequest({
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
    });
  });
});

// streamRequest
// cancelPreviouslyExecuting: false
// request success
test('when cancelPreviouslyExecuting is false if the same streamRequest is executed twice (and the response is slow), and request succeeds, both response callbacks are called', function(assert) {
  let request;
  assert.expect(2);
  const done = assert.async(2);

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  andThen(function() {
    return new Promise(function(resolve) {
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

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request.streamRequest({
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
    });
  });
});

// streamRequest
// cancelPreviouslyExecuting: false
// request fail
test('when cancelPreviouslyExecuting is false if the same streamRequest is executed twice (and the response is slow), and request fails, both error callbacks are called', function(assert) {
  let request;
  assert.expect(2);
  const done = assert.async(2);

  visit('/');
  andThen(() => {
    request = this.application.__container__.lookup('service:request');
  });

  andThen(function() {
    return new Promise(function(resolve) {
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

      later(() => {
        resolve();
      }, 500);

    }).then(() => {
      return request.streamRequest({
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
    });
  });
});
