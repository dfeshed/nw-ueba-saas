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

import { testSetupConfig, createTextPill } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment creation-keyboard',
  testSetupConfig
);

// Pills with specific operators

test('it creates pill with exists', function(assert) {
  createTextPill(this, 'foo', 'exists', '');
  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'foo exists', 'Expected exists.');
});

test('it creates pill with !exists', function(assert) {
  createTextPill(this, 'foo', '!exists', '');
  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'foo !exists', 'Expected !exists.');
});
