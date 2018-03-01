import { module, test } from 'qunit';

import {
  validatePackageConfig,
  validateLogConfigFields
} from 'packager/components/packager-form/validation-utils';

module('Unit | Util');

test('validatePackageConfig - valid', function(assert) {
  const formData = {
    port: '12',
    server: '1.1.1.1',
    serviceName: 'TEST',
    displayName: 'TEST',
    certificatePassword: 'CXXA123'
  };
  const error = validatePackageConfig(formData);
  assert.equal(error, null);
});

test('validatePackageConfig - invalid port', function(assert) {
  const formData = {
    server: '1.1.1.1',
    port: '12X'
  };
  const error = validatePackageConfig(formData);
  assert.deepEqual(error, {
    'invalidPortMessage': 'packager.errorMessages.invalidPort',
    'isPortError': true
  });
});

test('validatePackageConfig - invalid IP/hostname', function(assert) {
  const formData = {
    server: '-1.1.1.X',
    port: '123'
  };
  const error = validatePackageConfig(formData);
  assert.deepEqual(error, {
    'invalidServerMessage': 'packager.errorMessages.invalidServer',
    'isServerError': true
  });
});

test('validatePackageConfig - invalid serviceName', function(assert) {
  const formData = {
    server: '1.1.1.1',
    port: '12',
    certificatePassword: 'CXXA123',
    serviceName: 'TEST@_+',
    displayName: 'TEST'
  };
  const error = validatePackageConfig(formData);
  assert.deepEqual(error, {
    'invalidServiceNameMessage': 'packager.errorMessages.invalidName',
    'isServiceNameError': true
  });
});

test('validateLogConfigFields - valid', function(assert) {
  const formData = {
    configName: 'TEST',
    primaryDestination: 'aa',
    protocol: 'Test',
    channels: [{ channel: 'Security', filter: 'Include', eventId: '123' }]
  };
  const error = validateLogConfigFields(formData);
  assert.deepEqual(error, null);
});

test('validateLogConfigFields - invalid config name', function(assert) {
  const formData = {
    configName: 'TEST@!',
    primaryDestination: 'aa',
    protocol: 'Test',
    channels: []
  };
  const error = validateLogConfigFields(formData);
  assert.deepEqual(error, {
    'errorMessage': 'packager.specialCharacter',
    'isConfigError': true
  });
});

test('validateLogConfigFields - empty primaryDestination', function(assert) {
  const formData = {
    configName: 'TEST',
    protocol: 'Test',
    channels: []
  };
  const error = validateLogConfigFields(formData);
  assert.deepEqual(error, {
    'errorClass': 'is-error',
    'className': 'rsa-form-label is-error power-select'
  });
});

test('validateLogConfigFields - eventId out of range', function(assert) {
  const formData = {
    configName: 'TEST@!',
    primaryDestination: 'aa',
    protocol: 'Test',
    channels: [{ channel: 'Security', filter: 'Include', eventId: '123456789012' }]
  };
  const errorObj = {
    identifier: 1,
    reason: 'EVENT_ID_INVALID'
  };
  const error = validateLogConfigFields(formData, errorObj);
  assert.deepEqual(error, {
    'errorMessage': 'packager.specialCharacter',
    'isConfigError': true
  });
});

test('validateLogConfigFields - empty configName', function(assert) {
  const formData = {
    configName: '',
    protocol: 'Test',
    primaryDestination: 'aa',
    channels: [{ channel: 'Security', filter: 'Include', eventId: '1234' }]
  };
  const error = validateLogConfigFields(formData);
  assert.deepEqual(error, {
    isConfigError: true,
    errorMessage: 'packager.emptyName'
  });
});
