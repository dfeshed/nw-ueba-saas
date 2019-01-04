import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';
import { doubleClick } from '../pill-util';
import KEY_MAP from 'investigate-events/util/keys';

const ESCAPE_KEY = KEY_MAP.escape.code;
const DELETE_KEY = KEY_MAP.delete.code;
const BACKSPACE_KEY = KEY_MAP.backspace.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.code;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.code;
const ARROW_DOWN_KEY = KEY_MAP.arrowDown.code;
const ARROW_UP_KEY = KEY_MAP.arrowUp.code;
const modifiers = { shiftKey: true };


module('Integration | Component | complex-pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(find(PILL_SELECTORS.complexPill).textContent.trim(), 'FOOOOOOOO', 'text renders');
  });

  test('it sends a message when delete is clicked', async function(assert) {
    const done = assert.async();

    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });

    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETED, 'Message sent for pill delete is not correct');
      assert.deepEqual(
        data,
        { complexFilterText: 'FOOOOOOOO' },
        'Message sent for pill delete contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill delete contains correct pill position');
      done();
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.deletePill);
  });

  test('it renders as input when active/editable', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(find(PILL_SELECTORS.complexPillInput).value, 'FOOOOOOOO', 'input has value');
  });

  test('has proper class when active/editable', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.complexPillActive).length, 1, 'proper class present');
  });

  test('has proper class when pill is selected', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: true
    });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'proper class present');
  });

  test('has proper class when pill is focused', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isFocused: true
    });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
  });

  test('has proper class when invalid', async function(assert) {
    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isInvalid: true,
      validationError: {
        message: 'pill is invalid'
      }
    });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.invalidPill), 'class to indicate pill is invalid not present');
  });

  test('sends message to be selected when clicked', async function(assert) {
    assert.expect(3);

    this.set('handleMessage', (messageType, pillData, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_SELECTED, 'Message sent for pill delete is not correct');
      assert.deepEqual(pillData,
        { complexFilterText: 'FOOOOOOOO', isSelected: false },
        'Message sent for pill create contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
    });

    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: false
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.complexPill);
  });

  test('sends message to be deselected when selected and clicked', async function(assert) {
    assert.expect(3);

    this.set('handleMessage', (messageType, pillData, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DESELECTED, 'Message sent for pill delete is not correct');
      assert.deepEqual(pillData,
        { complexFilterText: 'FOOOOOOOO', isSelected: true },
        'Message sent for pill create contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
    });

    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: true
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.complexPill);
  });

  test('if active click does not send a message', async function(assert) {
    assert.expect(0);

    this.set('handleMessage', () => {
      assert.ok(false, 'should not get in here');
    });

    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.complexPill);
  });

  test('double clicks sends appropriate event', async function(assert) {
    const done = assert.async();
    const pillData = { complexFilterText: 'FOOOOOOOO' };
    this.set('handleMessage', (messageType, pD, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, 'Should be opened for edit');
      assert.ok(pillData === pD, 'should send out pill data');
      assert.ok(position === 0, 'should send out pill data');
      done();
    });
    this.set('pillData', pillData);

    await render(hbs`
      {{query-container/complex-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    doubleClick(PILL_SELECTORS.complexPill, true);
  });

  test('when active focus is placed in input', async function(assert) {
    const pillData = { complexFilterText: 'FOOOOOOOO' };
    this.set('handleMessage', () => {});
    this.set('pillData', pillData);

    await render(hbs`
      {{query-container/complex-pill
        isActive=true
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.complexPillInputFocus).length, 1, 'input has focus');
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    assert.expect(3);
    const pillData = { complexFilterText: 'FOOOOOOOO' };
    this.set('pillData', pillData);
    this.set('handleMessage', (messageType, pD, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_EDIT_CANCELLED, 'Edit should be cancelled');
      assert.ok(pillData === pD, 'should send out pill data');
      assert.ok(position === 0, 'should send out position');
    });
    await render(hbs`
      {{query-container/complex-pill
        isActive=true
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ESCAPE_KEY);
  });

  test('sends DELETE_PRESSED_ON_FOCUSED_PILL message up when focused and delete is pressed', async function(assert) {
    assert.expect(3);
    const pD = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL, 'should send out correct action');
      assert.ok(data === pD, 'should send out pill data');
    });
    this.set('pillData', pD);
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
  });

  test('sends DELETE_PRESSED_ON_FOCUSED_PILL message up when focused and backspace is pressed', async function(assert) {
    assert.expect(3);
    const pD = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL, 'should send out correct action');
      assert.ok(data === pD, 'should send out pill data');
    });
    this.set('pillData', pD);
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);
  });

  test('sends ENTER_PRESSED_ON_SELECTED_PILL message up when focused and enter is pressed', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL, 'should send out correct action');
      assert.ok(data === pillData, 'should send out pill data');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);
  });

  test('sends SELECT_ALL_PILLS_TO_LEFT message up when focused and shift and up arrow is pressed', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, position) => {
      assert.ok(messageType === MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, 'should send out correct message up');
      assert.equal(position, 0, 'should send out correct pill position');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_UP_KEY, modifiers);
  });

  test('sends SELECT_ALL_PILLS_TO_LEFT message up when focused and shift and left arrow is pressed', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, position) => {
      assert.ok(messageType === MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, 'should send out correct message up');
      assert.equal(position, 0, 'should send out correct pill position');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY, modifiers);
  });

  test('sends SELECT_ALL_PILLS_TO_RIGHT message up when focused and shift and down arrow is pressed', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, position) => {
      assert.ok(messageType === MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, 'should send out correct message up');
      assert.equal(position, 0, 'should send out correct pill position');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_DOWN_KEY, modifiers);
  });

  test('sends SELECT_ALL_PILLS_TO_RIGHT message up when focused and shift and right arrow is pressed', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, position) => {
      assert.ok(messageType === MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, 'should send out correct message up');
      assert.equal(position, 0, 'should send out correct pill position');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY, modifiers);
  });

  test('sends PILL_EDITED message up when pill active and enter is pressed on populated box', async function(assert) {
    assert.expect(2);

    const pillData = { complexFilterText: 'FOOOOOOOO', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_EDITED, 'should send out correct action');
      assert.ok(data.complexFilterText == pillData.complexFilterText, 'should send out pill data');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ENTER_KEY);
  });

  test('does not send PILL_EDITED message up when enter pressed with no text', async function(assert) {
    assert.expect(0);

    const pillData = { complexFilterText: 'F', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', () => {
      assert.notOk(true, 'should not get here');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    // remove the 1 character
    await fillIn(PILL_SELECTORS.complexPillInput, '');

    // submit for edit
    await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ENTER_KEY);
  });

  test('If on a focused pill and ARROW_LEFT is pressed, a message is sent up', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT, 'should send out correct message');
      assert.ok(position === 0, 'should send the correct position');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY);
  });

  test('If on a focused pill and ARROW_RIGHT is pressed, a message is sent up', async function(assert) {
    assert.expect(3);

    const pillData = { complexFilterText: 'FOOOOOOOO', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT, 'should send out correct message');
      assert.ok(position === 0, 'should send the correct position');
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY);
  });

});