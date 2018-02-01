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

import { testSetupConfig, createTextPill } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment interactivity-single-mouse-event',
  testSetupConfig
);

test('clicking on editable pills meta will bring up meta dropdown', function(assert) {
  const $fragment = createTextPill(this);
  $fragment.find('.meta').dblclick();
  const $input = $fragment.find('input');
  $input.get(0).setSelectionRange(2, 2);
  clickTrigger('.rsa-query-fragment');
  const $dropdownContent = $('.ember-basic-dropdown-content');
  assert.ok($dropdownContent.length === 1, 'Drop down appears when I click meta key in editable pill');
});
