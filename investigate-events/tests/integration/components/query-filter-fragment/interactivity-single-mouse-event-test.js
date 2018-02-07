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
import { moduleForComponent, test, skip } from 'ember-qunit';

import { testSetupConfig, PillHelpers } from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment interactivity-single-mouse-event',
  testSetupConfig
);

test('it is selectable on single click', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').click();
  assert.ok($fragment.hasClass('selected'), 'Expected fragment to be selected.');
  assert.ok($fragment.find('input').length === 0, 'Single click does not insert an input');
});

test('it is editable on double-click', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').dblclick();
  assert.ok($fragment.hasClass('edit-active'), 'Expected fragment to be editable.');
  assert.ok($fragment.find('input').length === 1, 'There is an input to edit');
});

test('it is not editable on double-click when it is a complex filter', function(assert) {
  const $fragment = PillHelpers.createTextPill(this, undefined, undefined, undefined, {
    complexFilter: 'foo'
  });
  $fragment.find('.meta').dblclick();
  assert.ok($fragment.hasClass('edit-active') === false, 'Expected fragment to not be editable.');
  assert.ok($fragment.find('input').length === 0, 'There is not an input to edit');
});

test('it places the cursor in the correct location on double-click', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').dblclick();
  const cursorPosition = this.$('input').get(0).selectionStart;
  const lengthOfText = 'action = \'foo\''.length;
  assert.ok(cursorPosition === lengthOfText, 'expect cursor to be at end of selection');
});

test('it stays editable for multiple double-clicks', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  $fragment.find('.meta').dblclick();
  $fragment.find('.meta').dblclick();
  assert.ok($fragment.hasClass('edit-active'), 'Expected fragment to be editable.');
  assert.ok($fragment.find('input').length === 1, 'There is an input to edit');
});

test('it is deletable on X click', function(assert) {
  const done = assert.async();
  const $fragment = PillHelpers.createTextPill(this, undefined, undefined, undefined, {
    deleteFilter(record) {
      assert.ok(record.id === 1001, 'Expected delete callback to be called with filter record details.');
      done();
    }
  });
  $fragment.find('.delete-filter-fragment').click();
});

//
// TODO: figure out how to test context menu
// the below isn't working
//
skip('has context menu', function(assert) {
  const $fragment = PillHelpers.createTextPill(this);
  // Have to first click the pill before you right click it
  $fragment.find('.meta').click();
  $fragment.find('.content-context-menu').contextmenu();
  assert.ok(this.$('#wormhole-context-menu').length === 1, 'Expected context menu to appear.');
});
