/*
 * These tests cover any formatting/manipulations
 * the component makes to user entered values.
 *
 * This includes formatting for display, then
 * re-formatting for editing.
 *
 * Tests should not include any interaction.
 */

import { moduleForComponent, test } from 'ember-qunit';

import { testSetupConfig, createTextPill } from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment formatting-text',
  testSetupConfig
);

test('it manually quotes when metaFormat is Text and quotes are not included', function(assert) {
  createTextPill(this, 'action', '=', 'foo');
  assert.equal(this.$('.meta').text().trim(), 'action = \'foo\'', 'Expected to be quoted.');
});
