/*
 * These tests cover basic test cases for pills
 * that have been rendered given a specific set of inputs.
 *
 * Tests should not include any interaction.
 */

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { testSetupConfig } from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment rendering-basic',
  testSetupConfig
);

test('it renders', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment}}`);
  const $el = this.$('.rsa-query-fragment');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when editActive', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment editActive=true}}`);
  const $el = this.$('.rsa-query-fragment.edit-active');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when selected', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment selected=true}}`);
  const $el = this.$('.rsa-query-fragment.selected');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when empty', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment empty=true}}`);
  const $el = this.$('.rsa-query-fragment.empty');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when typing', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment typing=true}}`);
  const $el = this.$('.rsa-query-fragment.typing');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when isExpensive', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment isExpensive=true}}`);
  const $el = this.$('.rsa-query-fragment.is-expensive');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

