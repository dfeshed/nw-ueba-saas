import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { getValidatorForExpression } from 'admin-source-management/reducers/usm/group-wizard-reducers';

module('Unit | Reducers | Group Wizard Reducers Utils', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('getValidatorForExpression(osType)', function(assert) {
    assert.equal(getValidatorForExpression(['osType', 'IN', []]), 'notEmpty', 'Validator for \'IN\' is \'notEmpty\' as expected');
  });

  test('getValidatorForExpression(osDescription)', function(assert) {
    assert.equal(getValidatorForExpression(['osDescription', 'EQUAL', []]), 'maxLength255', 'Validator for \'EQUAL\' is \'maxLength255\' as expected');
    assert.equal(getValidatorForExpression(['osDescription', 'CONTAINS', []]), 'maxLength255', 'Validator for \'CONTAINS\' is \'maxLength255\' as expected');
    assert.equal(getValidatorForExpression(['osDescription', 'STARTS_WITH', []]), 'maxLength255', 'Validator for \'STARTS_WITH\' is \'maxLength255\' as expected');
    assert.equal(getValidatorForExpression(['osDescription', 'ENDS_WITH', []]), 'maxLength255', 'Validator for \'ENDS_WITH\' is \'maxLength255\' as expected');
  });

  test('getValidatorForExpression(hostname)', function(assert) {
    assert.equal(getValidatorForExpression(['hostname', 'EQUAL', []]), 'validHostname', 'Validator for \'EQUAL\' is \'validHostname\' as expected');
    assert.equal(getValidatorForExpression(['hostname', 'CONTAINS', []]), 'validHostnameChars', 'Validator for \'CONTAINS\' is \'validHostnameChars\' as expected');
    assert.equal(getValidatorForExpression(['hostname', 'STARTS_WITH', []]), 'validHostnameChars', 'Validator for \'STARTS_WITH\' is \'validHostnameChars\' as expected');
    assert.equal(getValidatorForExpression(['hostname', 'ENDS_WITH', []]), 'validHostnameChars', 'Validator for \'ENDS_WITH\' is \'validHostnameChars\' as expected');
    assert.equal(getValidatorForExpression(['hostname', 'IN', []]), 'validHostnameList', 'Validator for \'ENDS_WITH\' is \'validHostnameList\' as expected');
  });

  test('getValidatorForExpression(ipv4)', function(assert) {
    assert.equal(getValidatorForExpression(['ipv4', 'BETWEEN', []]), 'validIPv4', 'Validator for \'BETWEEN\' is \'validIPv4\' as expected');
    assert.equal(getValidatorForExpression(['ipv4', 'IN', []]), 'validIPv4List', 'Validator for \'IN\' is \'validIPv4List\' as expected');
    assert.equal(getValidatorForExpression(['ipv4', 'NOT_IN', []]), 'validIPv4List', 'Validator for \'NOT_IN\' is \'validIPv4List\' as expected');
    assert.equal(getValidatorForExpression(['ipv4', 'NOT_BETWEEN', []]), 'validIPv4', 'Validator for \'NOT_BETWEEN\' is \'validIPv4\' as expected');
  });

  test('getValidatorForExpression(ipv6)', function(assert) {
    assert.equal(getValidatorForExpression(['ipv6', 'BETWEEN', []]), 'validIPv6', 'Validator for \'BETWEEN\' is \'validIPv6\' as expected');
    assert.equal(getValidatorForExpression(['ipv6', 'IN', []]), 'validIPv6List', 'Validator for \'IN\' is \'validIPv6List\' as expected');
    assert.equal(getValidatorForExpression(['ipv6', 'NOT_IN', []]), 'validIPv6List', 'Validator for \'NOT_IN\' is \'validIPv6List\' as expected');
    assert.equal(getValidatorForExpression(['ipv6', 'NOT_BETWEEN', []]), 'validIPv6', 'Validator for \'NOT_BETWEEN\' is \'validIPv6\' as expected');
  });

});
