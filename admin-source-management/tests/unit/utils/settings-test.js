import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { isBetween } from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-settings';

module('Unit | Utils | edr-settings', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('isBetween should return true or false correctly', function(assert) {
    let value = 5;
    let output = isBetween(value);
    assert.equal(output, true, `isBetween returns ${output} when input is ${value}`);
    value = -2;
    output = isBetween(value);
    assert.equal(output, false, `isBetween returns ${output} when input is ${value}`);
    value = 0;
    output = isBetween(value);
    assert.equal(output, false, `isBetween returns ${output} when input is ${value}`);
    value = 77777;
    output = isBetween(value);
    assert.equal(output, false, `isBetween returns ${output} when input is ${value}`);
    value = -77777;
    output = isBetween(value);
    assert.equal(output, false, `isBetween returns ${output} when input is ${value}`);
    value = '';
    output = isBetween(value);
    assert.equal(output, false, `isBetween returns ${output} when input is an empty string`);
  });
});
