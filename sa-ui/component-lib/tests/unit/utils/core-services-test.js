import { module, test } from 'qunit';
import { coreServiceNotUpdated } from 'component-lib/utils/core-services';

module('Unit | Utils | Core Services');

const minServiceVersion = '11.2';

test('coreServiceNotUpdated returns true when version lt minServiceVersion', function(assert) {
  const version = '11.1.0.0';
  assert.equal(coreServiceNotUpdated(version, minServiceVersion), true);
});

test('coreServiceNotUpdated returns false when version eq minServiceVersion', function(assert) {
  const version = '11.2.0.0';
  assert.equal(coreServiceNotUpdated(version, minServiceVersion), false);
});

test('coreServiceNotUpdated returns false when version gt minServiceVersion', function(assert) {
  const version = '11.3.0.0';
  assert.equal(coreServiceNotUpdated(version, minServiceVersion), false);
});
