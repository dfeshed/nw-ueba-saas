/*
 * These tests cover basic test cases for pills post-creation
 * to ensure we can create different kinds of pills.
 *
 * Tests should populate the pill as if the user
 * used the keyboard (vs the mouse)
 *
 * Tests should focus on the final pill created,
 * not on interim steps.
 */

import { moduleForComponent, test } from 'ember-qunit';

import { testSetupConfig, PillHelpers } from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment creation-keyboard',
  testSetupConfig
);

// Pills with specific operators

test('it creates pill with exists', function(assert) {
  PillHelpers.createTextPill(this, undefined, 'exists', '');
  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'alert exists', 'Expected exists.');
  assert.equal(this.$('input').length === 0, true, 'Expect no input box');
});

test('it creates pill with !exists', function(assert) {
  PillHelpers.createTextPill(this, undefined, '!exists', '');
  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'alert !exists', 'Expected !exists.');
  assert.equal(this.$('input').length === 0, true, 'Expect no input box');
});
