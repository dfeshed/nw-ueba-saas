import { module, test } from 'qunit';

import {
  isProgressBarDisabled,
  slowestInQuery,
  offlineServices,
  offlineServicesPath,
  hasOfflineServices,
  hasWarning,
  hasError,
  isConsoleEmpty,
  warningsWithServiceName,
  warningsPath,
  errorsWithServiceName,
  isComplete,
  decoratedDevices
} from 'investigate-events/reducers/investigate/query-stats/selectors';

module('Unit | Selectors | queryStats');

test('slowestInQuery when only one', function(assert) {
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

test('slowestInQuery when only multiple', function(assert) {
  const slowest = slowestInQuery({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          elapsedTime: 1
        }, {
          serviceId: 'bar',
          elapsedTime: 1
        }]
      }
    }
  });

  assert.equal(slowest.length, 2);
  assert.equal(slowest[0], 'foo');
  assert.equal(slowest[1], 'bar');
});

test('offlineServicesPath', function(assert) {
  assert.equal(offlineServicesPath({
    investigate: {
      queryStats: {
        devices: []
      }
    }
  }).length, 0);

  const offlinePath = offlineServicesPath({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: true,
          devices: [{
            serviceId: 'bar',
            on: false
          }]
        }]
      }
    }
  });
  assert.equal(offlinePath.length, 2);
  assert.equal(offlinePath[0], 'bar');
  assert.equal(offlinePath[1], 'foo');

  assert.equal(offlineServicesPath({
    investigate: {
      queryStats: {
        devices: [{
          on: true
        }]
      }
    }
  }).length, 0);
});

test('offlineServices', function(assert) {
  assert.equal(offlineServices({
    investigate: {
      queryStats: {
        devices: []
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  }).length, 0);

  const offlineServicesArraySingle = offlineServices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: true,
          devices: [{
            serviceId: 'bar',
            on: false
          }]
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  });
  assert.equal(offlineServicesArraySingle.length, 1);
  assert.equal(offlineServicesArraySingle[0], 'bar');

  const offlineServicesArrayMultiple = offlineServices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: false
        }, {
          serviceId: 'bar',
          on: false
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  });
  assert.equal(offlineServicesArrayMultiple.length, 2);
  assert.ok(offlineServicesArrayMultiple.includes('foo'));
  assert.ok(offlineServicesArrayMultiple.includes('bar'));

  assert.equal(offlineServices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: true
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  }).length, 0);
});

test('hasOfflineServices', function(assert) {
  assert.equal(hasOfflineServices({
    investigate: {
      queryStats: {
        devices: []
      },
      services: {
        serviceData: []
      }
    }
  }), false, '1');

  assert.equal(hasOfflineServices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: false
        }, {
          serviceId: 'bar',
          on: false
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  }), true, 'a');

  assert.equal(hasOfflineServices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: false
        }, {
          serviceId: 'bar',
          on: true
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        }, {
          id: 'bar',
          displayName: 'bar'
        }]
      }

    }
  }), true, 'b');

  assert.equal(hasOfflineServices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: true
        }, {
          serviceId: 'bar',
          on: true
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  }), false, 'c');

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
        devices: [{}]
      }
    }
  }), true);

  assert.equal(isComplete({
    investigate: {
      queryStats: {
        devices: []
      }
    }
  }), false);

  assert.equal(isComplete({
    investigate: {
      queryStats: {
        percent: 100
      }
    }
  }), false);

  assert.equal(isComplete({
    investigate: {
      queryStats: {
        errors: [{}]
      }
    }
  }), true);
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

test('warningsPath', function(assert) {
  assert.equal(warningsPath({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: true,
          devices: [{
            serviceId: 'bar',
            on: true
          }]
        }],
        warnings: []
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        }, {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  }).length, 0);

  const warningsPathResult = warningsPath({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          on: true,
          devices: [{
            serviceId: 'bar',
            on: true
          }]
        }],
        warnings: [{
          serviceId: 'bar',
          warning: 'warningBar'
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        }, {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  });
  assert.equal(warningsPathResult.length, 2);
  assert.equal(warningsPathResult[0], 'bar');
  assert.equal(warningsPathResult[1], 'foo');
});

test('errorsWithServiceName', function(assert) {
  const decoratedErrors = errorsWithServiceName({
    investigate: {
      services: {
        serviceData: [
          { id: 'foo', displayName: 'Service Name', name: 'SN' }
        ]
      },
      queryStats: {
        errors: [{
          serviceId: 'foo',
          error: 'error'
        }]
      }
    }
  });

  assert.equal(decoratedErrors.length, 1);
  assert.equal(decoratedErrors[0].serviceName, 'Service Name');
  assert.equal(decoratedErrors[0].error, 'error');
  assert.equal(decoratedErrors[0].serviceId, 'foo');
});

test('decoratedDevices', function(assert) {
  assert.equal(decoratedDevices({
    investigate: {
      queryStats: {},
      services: {}
    }
  }).length, 0);

  const devices = decoratedDevices({
    investigate: {
      queryStats: {
        devices: [{
          serviceId: 'foo',
          elapsedTime: 1000,
          on: true,
          devices: [{
            serviceId: 'bar',
            elapsedTime: 2000,
            on: true
          }]
        }]
      },
      services: {
        serviceData: [{
          id: 'foo',
          displayName: 'foo'
        },
        {
          id: 'bar',
          displayName: 'bar'
        }]
      }
    }
  });

  assert.equal(devices.length, 1);
  assert.equal(devices[0].serviceName, 'foo');
  assert.equal(devices[0].serviceId, 'foo');
  assert.equal(devices[0].elapsedTime, 1);
  assert.equal(devices[0].on, true);
  assert.equal(devices[0].devices.length, 1);

  assert.equal(devices[0].devices[0].serviceName, 'bar');
  assert.equal(devices[0].devices[0].serviceId, 'bar');
  assert.equal(devices[0].devices[0].elapsedTime, 2);
  assert.equal(devices[0].devices[0].on, true);
  assert.equal(devices[0].devices[0].devices.length, 0);
});
