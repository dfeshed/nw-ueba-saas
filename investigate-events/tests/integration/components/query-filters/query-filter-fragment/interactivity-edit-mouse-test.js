/* global $ */
/*
 * These tests start with a pill, and test the
 * effect of a single MOUSE event on the pill.
 *
 * Clicking, double clicking, clicking an x, etc.
 *
 * These tests DO NOT include what happens after
 * the single event (editing) or keyboard events
 * (pressing <- selecting pill)
 *
 */
import { moduleForComponent, test } from 'ember-qunit';
import { clickTrigger } from 'ember-power-select/test-support/helpers';
import {
  testSetupConfig,
  PillHelpers,
  setCursorAtMeta,
  setCursorAtOperator,
  setCursorAtValue
} from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment interactivity-edit-mouse',
  testSetupConfig
);

test('clicking on editable pills meta will bring up meta dropdown', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').dblclick();
  const $input = $fragment.find('input');

  // Putting cursor at 2nd character to trigger meta drop down
  setCursorAtMeta($input);
  clickTrigger('.rsa-query-fragment');

  const $dropdownContent = $('.ember-basic-dropdown-content');
  assert.ok($dropdownContent.length === 1, 'Drop down appears when I click meta key in editable pill');

  const $dropdownItems = $dropdownContent.find('li');
  assert.ok($dropdownItems.length === 12, 'Drop down has appropriate count of items');
});

test('clicking on editable pills meta will bring up operator dropdown', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').dblclick();
  const $input = $fragment.find('input');

  // Putting cursor at 8th character to trigger operator drop down
  setCursorAtOperator($input, 'Text');
  clickTrigger('.rsa-query-fragment');

  const $dropdownContent = $('.ember-basic-dropdown-content');
  assert.ok($dropdownContent.length === 1, 'Drop down appears when I click selector in editable pill');

  const $dropdownItems = $dropdownContent.find('li');
  assert.ok($dropdownItems.length === 7, 'The correct number of operators for that meta are visible');
});

test('clicking on editable pills value will not bring up any dropdown', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').dblclick();
  const $input = $fragment.find('input');

  setCursorAtValue($input, 'Text', '=');
  clickTrigger('.rsa-query-fragment');

  const $dropdownContent = $('.ember-basic-dropdown-content');
  assert.ok($dropdownContent.find('li').length === 0, 'Drop down does not appear when I click selector in editable pill');
});
