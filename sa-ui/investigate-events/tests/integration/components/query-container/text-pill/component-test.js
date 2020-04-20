import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers'; //eslint-disable-line
import { doubleClick } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';

const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.key;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.key;
const BACKSPACE_KEY = KEY_MAP.backspace.key;
const DELETE_KEY = KEY_MAP.delete.key;
const ENTER_KEY = KEY_MAP.enter.key;
const ESCAPE_KEY = KEY_MAP.escape.key;
const HOME_KEY = KEY_MAP.home.key;
const END_KEY = KEY_MAP.end.key;
const modifiers = { shiftKey: true };

module('Integration | Component | query-container/text-pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    this.set('pillData', { searchTerm: 'foo bar' });
    await render(hbs`
      {{query-container/text-pill
        pillData=pillData
      }}
    `);
    assert.equal(find(PILL_SELECTORS.textPill).textContent.trim(), 'foo bar', 'text pill was not rendered');
    assert.ok(find(PILL_SELECTORS.deletePill), 'delete icon missing');
  });

  test('it has icon to indicate text pill', async function(assert) {
    this.set('pillData', { searchTerm: 'foo bar' });
    await render(hbs`
      {{query-container/text-pill
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.textPillIcon), 'icon was not found');
  });

  test('it renders as input when active and has focus', async function(assert) {
    this.set('pillData', { searchTerm: 'foo bar' });
    await render(hbs`
      {{query-container/text-pill
        isActive=true
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.textPillActive), 'pill not set to active');
    assert.equal(find(PILL_SELECTORS.textPillInput).value, 'foo bar', 'input not rendered or missing value');
    assert.ok(find(PILL_SELECTORS.textPillInputFocus), 'input missing focus');
  });

  test('it can be focused', async function(assert) {
    this.set('pillData', { searchTerm: 'foo bar', isFocused: true });
    await render(hbs`
      {{query-container/text-pill
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.focusedPill), 'focused pill not found');
  });

  test('it can be selected', async function(assert) {
    this.set('pillData', { searchTerm: 'foo bar', isSelected: true });
    await render(hbs`
      {{query-container/text-pill
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.selectedPill), 'selected pill not found');
  });

  test('sends message to be selected when clicked', async function(assert) {
    const done = assert.async();
    const pillData = { searchTerm: 'foo bar' };
    this.set('pillData', pillData);
    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_SELECTED, 'Message type is incorrect');
      assert.deepEqual(data, pillData, 'Message pillData is incorrect');
      assert.equal(position, 0, 'Message position is incorrect');
      done();
    });
    await render(hbs`
      {{query-container/text-pill
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.textPill);
  });

  test('double clicks sends appropriate event', async function(assert) {
    const done = assert.async();
    const pillData = { searchTerm: 'foo bar' };
    this.set('pillData', pillData);
    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, 'Message type is incorrect');
      assert.deepEqual(data, pillData, 'Message data is incorrect');
      assert.equal(position, 0, 'Message position is incorrect');
      done();
    });
    await render(hbs`
      {{query-container/text-pill
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    doubleClick(PILL_SELECTORS.textPill, true);
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    assert.expect(3);
    const pillData = { searchTerm: 'foo bar' };
    this.set('pillData', pillData);
    this.set('handleMessage', (messageType, pD, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_EDIT_CANCELLED, 'Edit should be cancelled');
      assert.ok(pillData === pD, 'should send out pill data');
      assert.ok(position === 0, 'should send out position');
    });
    await render(hbs`
      {{query-container/text-pill
        isActive=true
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', ESCAPE_KEY);
  });

  test('sends message up when focused and delete is pressed', async function(assert) {
    assert.expect(6);
    const pD = { searchTerm: 'foo bar', isFocused: true };
    this.set('handleMessage', (messageType, data, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, 'should send out correct action');
      assert.ok(data, 'should send out pill data');
      assert.ok(data.isDeleteEvent, 'should be a delete event');
      assert.ok(data.isFocusedPill, 'should be a focused pill');
      assert.equal(position, 0, 'correct position is passed');
    });
    this.set('pillData', pD);
    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
  });

  test('sends message up when focused and backspace is pressed', async function(assert) {
    assert.expect(6);
    const pD = { searchTerm: 'foo bar', isFocused: true };
    this.set('handleMessage', (messageType, data, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, 'should send out correct action');
      assert.ok(data, 'should send out pill data');
      assert.ok(data.isBackspaceEvent, 'should be a backspace event');
      assert.ok(data.isFocusedPill, 'should be a focused pill');
      assert.equal(position, 0, 'correct position is passed');
    });
    this.set('pillData', pD);
    await render(hbs`
      {{query-container/text-pill
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

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL, 'should send out correct action');
      assert.ok(data === pillData, 'should send out pill data');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);
  });


  test('sends SELECT_ALL_PILLS_TO_LEFT message up when focused and shift and left arrow is pressed', async function(assert) {
    assert.expect(4);

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data, position) => {
      assert.ok(messageType === MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, 'should send out correct message up');
      assert.equal(position, 0, 'should send out correct pill position');
      assert.notOk(data, 'Data should not be passed');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY, modifiers);
  });

  test('sends SELECT_ALL_PILLS_TO_RIGHT message up when focused and shift and right arrow is pressed', async function(assert) {
    assert.expect(4);

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data, position) => {
      assert.ok(messageType === MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, 'should send out correct message up');
      assert.equal(position, 0, 'should send out correct pill position');
      assert.notOk(data, 'Data should not be passed');
    });

    await render(hbs`
      {{query-container/text-pill
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

    const pillData = { searchTerm: 'foo bar', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_EDITED, 'should send out correct action');
      assert.ok(data.searchTerm == pillData.searchTerm, 'should send out pill data');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', ENTER_KEY);
  });

  test('does not send PILL_EDITED message up when enter pressed with no text', async function(assert) {
    assert.expect(0);

    const pillData = { searchTerm: 'F', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', () => {
      assert.notOk(true, 'should not get here');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    // remove the 1 character
    await fillIn(PILL_SELECTORS.textPillInput, '');

    // submit for edit
    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', ENTER_KEY);
  });

  test('If on a focused pill and ARROW_LEFT is pressed, a message is sent up', async function(assert) {
    assert.expect(4);

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT, 'should send out correct message');
      assert.ok(position === 0, 'should send the correct position');
      assert.notOk(data, 'Data should not be passed');
    });

    await render(hbs`
      {{query-container/text-pill
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
    assert.expect(4);

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT, 'should send out correct message');
      assert.ok(position === 0, 'should send the correct position');
      assert.notOk(data, 'Data should not be passed');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY);
  });

  test('If on a focused pill and HOME is pressed, a message is sent up', async function(assert) {
    assert.expect(3);

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_HOME_PRESSED, 'should send out correct message');
      assert.deepEqual(
        data,
        { searchTerm: 'foo bar', isFocused: true },
        'Message sent for pill contains correct pill data'
      );
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', HOME_KEY);
  });

  test('If on a focused pill and END is pressed, a message is sent up', async function(assert) {
    assert.expect(3);

    const pillData = { searchTerm: 'foo bar', isFocused: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_END_PRESSED, 'should send out correct message');
      assert.deepEqual(
        data,
        { searchTerm: 'foo bar', isFocused: true },
        'Message sent for pill contains correct pill data'
      );
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', END_KEY);
  });

  test('In Edit mode and no text sends  message up when home is pressed', async function(assert) {
    assert.expect(2);

    const pillData = { searchTerm: 'foo bar', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_HOME_PRESSED, 'should send out correct action');
      assert.ok(data.searchTerm == pillData.searchTerm, 'should send out pill data');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    // remove the 1 character
    await fillIn(PILL_SELECTORS.textPillInput, '');
    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', HOME_KEY);
  });

  test('In Edit mode and no text sends  message up when end is pressed', async function(assert) {
    assert.expect(2);

    const pillData = { searchTerm: 'foo bar', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', (messageType, data) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_END_PRESSED, 'should send out correct action');
      assert.ok(data.searchTerm == pillData.searchTerm, 'should send out pill data');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    // remove the 1 character
    await fillIn(PILL_SELECTORS.textPillInput, '');
    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', END_KEY);
  });

  test('In Edit mode and with text does not send message up when home or end  is pressed', async function(assert) {
    assert.expect(0);

    const pillData = { searchTerm: 'F', isEditing: true };
    this.set('pillData', pillData);

    this.set('handleMessage', () => {
      assert.notOk(true, 'should not get here');
    });

    await render(hbs`
      {{query-container/text-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', HOME_KEY);
    await triggerKeyEvent(PILL_SELECTORS.textPillInput, 'keydown', END_KEY);
  });
});
