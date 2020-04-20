import { module, test } from 'qunit';

import { validatePackageConfig } from 'packager/components/packager-form/validation-utils';

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

test('validatePackageConfig when port is passed which is not within range', function(assert) {
  const result1 = validatePackageConfig({ port: 0 });
  const expectedResult = {
    isPortError: true,
    invalidPortMessage: 'packager.errorMessages.invalidPort'
  };
  assert.deepEqual(result1, expectedResult);

  const result2 = validatePackageConfig({ port: 65536 });
  assert.deepEqual(result2, expectedResult);
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
    'isServiceNameError': true,
    'isAccordion': true
  });
});