import { module, test } from 'qunit';

import {
  isProgressBarDisabled,
  slowestInQuery,
  offlineServices,
  hasWarning,
  hasError,
  serviceshasErrorOrWarning,
  isConsoleEmpty,
  warningsWithServiceName,
  errorsWithServiceName,
  isComplete
} from 'investigate-events/reducers/investigate/query-stats/selectors';

module('Unit | Selectors | queryStats');

test('slowestInQuery', function(assert) {
  const slowest = slowestInQuery({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          elapsedTime: 1
        }, {
          serviceId: 'bar',
          elapsedTime: 2
        }]
      }
    }
  });

  assert.equal(slowest.length, 1);
  assert.equal(slowest[0], 'bar');
});

test('offlineServices', function(assert) {
  assert.equal(offlineServices({
    investigate: {
      queryStats: {
        devices: []
      }
    }
  }).length, 0);

  assert.equal(offlineServices({
    investigate: {
      queryStats: {
        devices: [{
          on: false
        }]
      }
    }
  }).length, 1);

  assert.equal(offlineServices({
    investigate: {
      queryStats: {
        devices: [{
          on: true
        }]
      }
    }
  }).length, 0);
});

test('hasError', function(assert) {
  assert.equal(hasError({
    investigate: {
      queryStats: {
        errors: ['foo']
      }
    }
  }), true);

  assert.equal(hasError({
    investigate: {
      queryStats: {
        errors: []
      }
    }
  }), false);

  assert.equal(hasError({
    investigate: {
      queryStats: {
        devices: [{
          on: false
        }]
      }
    }
  }), true);

  assert.equal(hasError({
    investigate: {
      queryStats: {
        devices: [{
          on: true
        }]
      }
    }
  }), false);
});

test('hasWarning', function(assert) {
  assert.equal(hasWarning({
    investigate: {
      queryStats: {
        warnings: ['warning']
      }
    }
  }), true);

  assert.equal(hasWarning({
    investigate: {
      queryStats: {
        warnings: [],
        percent: 100
      }
    }
  }), false);

});

test('serviceshasErrorOrWarning', function(assert) {
  const serviceIds = serviceshasErrorOrWarning({
    investigate: {
      queryStats: {
        errors: [{
          serviceId: 'foo'
        }],
        warnings: [{
          serviceId: 'bar'
        }]
      }
    }
  });

  assert.equal(serviceIds[0], 'foo');
  assert.equal(serviceIds[1], 'bar');
  assert.equal(serviceIds.length, 2);
});

test('isConsoleEmpty', function(assert) {
  assert.equal(isConsoleEmpty({
    investigate: {
      queryStats: {
        description: null
      }
    }
  }), true);

  assert.equal(isConsoleEmpty({
    investigate: {
      queryStats: {
        description: 'foo'
      }
    }
  }), false);
});

test('isComplete', function(assert) {
  assert.equal(isComplete({
    investigate: {
      queryStats: {
        percent: 100,
        devices: [{}]
      }
    }
  }), true);

  assert.equal(isComplete({
    investigate: {
      queryStats: {
        percent: 100,
        devices: []
      }
    }
  }), false);

  assert.equal(isComplete({
    investigate: {
      queryStats: {
        percent: 99
      }
    }
  }), false);
});


test('isProgressBarDisabled', function(assert) {
  assert.equal(isProgressBarDisabled({
    investigate: {
      queryStats: {
        description: 'Queued',
        percent: 0
      }
    }
  }), true);

  assert.equal(isProgressBarDisabled({
    investigate: {
      queryStats: {
        description: 'Queued',
        percent: 50
      }
    }
  }), false);

  assert.equal(isProgressBarDisabled({
    investigate: {
      queryStats: {
        description: 'Executing',
        percent: 0
      }
    }
  }), false);
});

test('warningsWithServiceName', function(assert) {
  const decoratedWarnings = warningsWithServiceName({
    investigate: {
      queryStats: {
        warnings: [{
          serviceId: 'foo',
          warning: 'warning'
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        }]
      }

    }
  });

  assert.equal(decoratedWarnings.length, 1);
  assert.equal(decoratedWarnings[0].serviceName, 'foo');
  assert.equal(decoratedWarnings[0].warning, 'warning');
  assert.equal(decoratedWarnings[0].serviceId, 'foo');
});

test('errorsWithServiceName', function(assert) {
  const decoratedErrors = errorsWithServiceName({
    investigate: {
      queryStats: {
        errors: [{
          serviceId: 'foo',
          error: 'error'
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        }]
      }

    }
  });

  assert.equal(decoratedErrors.length, 1);
  assert.equal(decoratedErrors[0].serviceName, 'foo');
  assert.equal(decoratedErrors[0].error, 'error');
  assert.equal(decoratedErrors[0].serviceId, 'foo');
});
