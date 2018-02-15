/* global $ */
import { moduleForComponent, test, skip } from 'ember-qunit';
import { testSetupConfig, PillHelpers, pressEnter } from './util';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import wait from 'ember-test-helpers/wait';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment interactivity-create-mouse',
  testSetupConfig
);

test('mouse interaction only', function(assert) {

  PillHelpers.createPillWithFormat(this, 'UInt64', { createPill: false });
  // drop down should appear
  this.$('input').focus();
  assert.ok(this.$('.ember-power-select-trigger').length === 1, 'Drop down for meta appears on focus');
  assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 11, 'Has the correct number of mocked meta keys');

  // choose a meta option from drop down
  // sessionid is special case so will have 4 operators
  selectChoose('.ember-power-select-trigger', 'sessionid');

  return wait().then(() => {
    // operator drop down should appear with correct number of operators
    // choose an operator
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 4, 'Correct count for operators based on format');
    selectChoose('.ember-power-select-trigger', 'exists');

    return wait().then(() => {
      assert.equal(this.$('.meta').text().trim(), 'sessionid exists', 'Expected filter.');

    });
  });
});

// There are 3 actions that are being tested here
// 1. To select a meta name from the dropdown using mouse
// 2. To select operator from the next dropdown
// 3. Type in the value and press Enter
// Can't find a way to execute step 3. Skipping this test for now as we need to find out a way to emulate typing a character.
// We do not want input.val(), which removes the existing text and pastes the current one, and makes our mouse selection to be wiped out from the input box.
skip('mouse interaction with minimal keyboard', function(assert) {

  PillHelpers.createPillWithFormat(this, 'UInt64', { createPill: false });

  // eslint-disable-next-line new-cap
  const eDown = $.Event('keydown');
  // eslint-disable-next-line new-cap
  const eUp = $.Event('keyup');
  // keycode for `1`
  eDown.keyCode = 49;
  eDown.code = 'Digit1';
  eUp.keyCode = 49;
  eUp.code = 'Digit1';

  // drop down should appear
  this.$('input').focus();
  assert.ok(this.$('.ember-power-select-trigger').length === 1, 'Drop down for meta appears on focus');
  assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 11, 'Has the correct number of mocked meta keys');

  // choose a meta option from drop down
  selectChoose('.ember-power-select-trigger', 'sessionid');

  return wait().then(() => {
    // operator drop down should appear with correct number of operators
    // choose an operator
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 8, 'Correct count for operators based on format');
    selectChoose('.ember-power-select-trigger', '=');

    // need to emulate typing a character in the input box
    // which already has meta and operator
    // Typing a character and not using input.val() for it, is the next task to make it work
    this.$('input').trigger(eDown);
    this.$('input').trigger(eUp);
    pressEnter(this.$('input'));
    assert.equal(this.$('.meta').text().trim(), 'sessionid = 1', 'Expected filter.');

  });
});