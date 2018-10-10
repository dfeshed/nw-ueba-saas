import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import _ from 'lodash';
import { descriptionsForDisplay, groupExpressionValidator } from 'admin-source-management/reducers/usm/util/selector-helpers';

module('Unit | Utils | reducers/usm/util/selector-helpers', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('descriptionsForDisplay() should properly handle a null description', function(assert) {
    const description = null;
    const expectedDescriptions = {
      truncated: null,
      truncatedWithEllipsis: null
    };
    const actualDescriptions = descriptionsForDisplay(description);
    assert.deepEqual(actualDescriptions, expectedDescriptions, 'description is null as expected');
  });

  test('descriptionsForDisplay() should return an object containing two truncated versions of a description <= 256 chars', function(assert) {
    const description = 'Short Description eh!';
    const expectedDescriptions = {
      truncated: description,
      truncatedWithEllipsis: description
    };
    const actualDescriptions = descriptionsForDisplay(description);
    assert.deepEqual(actualDescriptions, expectedDescriptions, `description is ${description} as expected`);
  });

  test('descriptionsForDisplay() should return an object containing two truncated versions of a description > 256 chars', function(assert) {
    const longDescription445 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
    const expectedDescriptions256 = {
      truncated: _.truncate(longDescription445, { length: 256, omission: '' }),
      truncatedWithEllipsis: _.truncate(longDescription445, { length: 256, omission: '...' })
    };
    const actualDescriptions256 = descriptionsForDisplay(longDescription445);
    assert.deepEqual(actualDescriptions256, expectedDescriptions256, 'description is truncated as expected');
  });

  test('groupExpressionValidator(none)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'none', true), { isError: false, showError: false }, 'empty validation is good as expected');
    assert.deepEqual(groupExpressionValidator('not empty', 'none', true), { isError: false, showError: false }, '\'not empty\' validation is good as expected');
    assert.deepEqual(groupExpressionValidator(['not empty'], 'none', true), { isError: false, showError: false }, '[\'not empty\'] validation is good as expected');
  });

  test('groupExpressionValidator(notEmpty)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'notEmpty', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([], 'notEmpty', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([''], 'notEmpty', true), { isError: true, showError: true }, '[\'\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('not empty', 'notEmpty', true), { isError: false, showError: false }, '\'not empty\' validation is good as expected');
  });

  test('groupExpressionValidator(validHostname)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validHostname', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([], 'validHostname', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('-error', 'validHostname', true), { isError: true, showError: true }, '\'-error\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('ABC', 'validHostname', true), { isError: false, showError: false }, '\'ABC\', validation is good as expected');
    // assert.deepEqual(groupExpressionValidator('W123456789-0123456789-0123456789-0123456789-0123456789-0123456789', 'validHostname', true), { isError: true, showError: true }, '\'W123456789-0123456789-0123456789-0123456789-0123456789-0123456789\', validation is bad as expected, too long');
  });

  test('groupExpressionValidator(validHostnameList)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validHostnameList', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([], 'validHostnameList', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([''], 'validHostnameList', true), { isError: true, showError: true }, '[\'\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('ABC, 123$, GOOD', 'validHostnameList', true), { isError: true, showError: true }, '\'ABC, 123$, GOOD\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('ABC, 1WW.com', 'validHostnameList', true), { isError: false, showError: false }, '\'ABC, 1WW\' validation is good as expected');
  });

  test('groupExpressionValidator(validHostnameChars)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validHostnameChars', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([], 'validHostnameChars', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([''], 'validHostnameChars', true), { isError: true, showError: true }, '[\'\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('123$', 'validHostnameChars', true), { isError: true, showError: true }, '\'123$\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('ABC', 'validHostnameChars', true), { isError: false, showError: false }, '\'ABC\ validation is good as expected');
  });

  test('groupExpressionValidator(validIPv4)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validIPv4', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([''], 'validIPv4', true), { isError: true, showError: true }, '[\'\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('1,2,3,4', 'validIPv4', true), { isError: true, showError: true }, '\'1,2,3,4\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('999.999.999.999', 'validIPv4', true), { isError: true, showError: true }, '\'999.999.999.999\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('1.2.3.4   ', 'validIPv4', true), { isError: true, showError: true }, '\'1.2.3.4   \' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('1.2.3.4', 'validIPv4', true), { isError: false, showError: false }, '\'1.2.3.4\' validation is good as expected');
    assert.deepEqual(groupExpressionValidator(['1,2,3,4', '5,6,7,8'], 'validIPv4', true), { isError: true, showError: true }, '[\'1,2,3,4\', \'5,6,7,8\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator(['1.2.3.4', '5.6.7.8'], 'validIPv4', true), { isError: false, showError: false }, '[\'1.2.3.4\', \'5.6.7.8\'] validation is good as expected');
  });

  test('groupExpressionValidator(validIPv4List)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validIPv4List', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([], 'validIPv4List', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([''], 'validIPv4List', true), { isError: true, showError: true }, '[\'\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('1,2,3,4 5,6,7,8 1,2,3,4, 3.3.3.3', 'validIPv4List', true), { isError: true, showError: true }, '\'1,2,3,4 5,6,7,8 1,2,3,4, 3.3.3.3\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('1.2.3.4, 2.3.4.5 3.4.5.6', 'validIPv4List', true), { isError: false, showError: false }, '\'1.2.3.4, 2.3.4.5 3.4.5.6\' validation is good as expected');
  });

  test('groupExpressionValidator(validIPv6)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validIPv6', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([], 'validIPv6', true), { isError: true, showError: true }, 'empty validation is bad as expected');
    assert.deepEqual(groupExpressionValidator([''], 'validIPv6', true), { isError: true, showError: true }, '[\'\'] validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('1,2,3,4', 'validIPv6', true), { isError: true, showError: true }, '\'1,2,3,4\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('::1', 'validIPv6', true), { isError: false, showError: false }, '\'::1\' validation is good as expected');
    assert.deepEqual(groupExpressionValidator('::1  ', 'validIPv6', true), { isError: true, showError: true }, '\'::1  \' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('2001:0db8:85a3:0000:0000:8a2e:0370:7334', 'validIPv6', true), { isError: false, showError: false }, '\'2001:0db8:85a3:0000:0000:8a2e:0370:7334\' validation is good as expected');
    assert.deepEqual(groupExpressionValidator('1200::AB00:1234::2552:7777:1313', 'validIPv6', true), { isError: true, showError: true }, '\'1200::AB00:1234::2552:7777:1313\' validation is bad as expected');
  });

  test('groupExpressionValidator(validIPv6List)', function(assert) {
    assert.deepEqual(groupExpressionValidator('', 'validIPv6List', true), { isError: true, showError: true }, 'empty validation is as expected');
    assert.deepEqual(groupExpressionValidator([], 'validIPv6List', true), { isError: true, showError: true }, 'empty validation is as expected');
    assert.deepEqual(groupExpressionValidator([''], 'validIPv6List', true), { isError: true, showError: true }, 'bad [\'\'] validation is as expected');
    assert.deepEqual(groupExpressionValidator('1,2,3,4 2001:db8:0:200:0:0:0:7 2001:0db8:85a3:0000:0000:8a2e:0370:7334', 'validIPv6List', true), { isError: true, showError: true }, '\'1,2,3,4 2001:db8:0:200:0:0:0:7 2001:0db8:85a3:0000:0000:8a2e:0370:7334\' validation is bad as expected');
    assert.deepEqual(groupExpressionValidator('2001:db8:0:200:0:0:0:7, 2001:db8:0:200:0:0:0:8 2001:db8:0:200:0:0:0:9', 'validIPv6List', true), { isError: false, showError: false }, '\'2001:db8:0:200:0:0:0:7, 2001:db8:0:200:0:0:0:8 2001:db8:0:200:0:0:0:9\' validation is good as expected');
  });
});
