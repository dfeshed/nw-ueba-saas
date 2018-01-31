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

import { testSetupConfig, createTextPill } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment interactivity-single-mouse-event',
  testSetupConfig
);

test('it is selectable on single click', function(assert) {
  const $fragment = createTextPill(this);
  $fragment.find('.meta').click();
  assert.ok($fragment.hasClass('selected'), 'Expected fragment to be selected.');
});

test('it is editable on double-click', function(assert) {
  const $fragment = createTextPill(this);
  $fragment.find('.meta').dblclick();
  assert.ok($fragment.hasClass('edit-active'), 'Expected fragment to be editable.');
});