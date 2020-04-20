import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, triggerKeyEvent } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const ARROW_LEFT = KEY_MAP.arrowLeft.key;
const ARROW_RIGHT = KEY_MAP.arrowRight.key;
const BACKSPACE = KEY_MAP.backspace.key;
const DELETE_KEY = KEY_MAP.delete.key;
const ENTER = KEY_MAP.enter.key;
const XKey = 88;
const HOME_KEY = KEY_MAP.home.key;
const END_KEY = KEY_MAP.end.key;
const OPEN_PAREN = KEY_MAP.openParen.key;
const UPPERCASE_A = 'A';
const LOWERCASE_A = 65;

module('Integration | Component | focus-holder', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it sends a message when delete is pressed', async function(assert) {
    assert.expect(4);

    this.set('sendMessage', (messageType, data) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, 'the correct message type is sent when delete is pressed');
      assert.ok(data, 'should send out pill data');
      assert.ok(data.isDeleteEvent, 'should be a delete event');
      assert.ok(data.isFocusedPill, 'should be a focused pill');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
  });

  test('it sends a message when Backspace is pressed', async function(assert) {
    assert.expect(4);

    this.set('sendMessage', (messageType, data) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, 'the correct message type is sent when Backspace is pressed');
      assert.ok(data, 'should send out pill data');
      assert.ok(data.isBackspaceEvent, 'should be a backspace event');
      assert.ok(data.isFocusedPill, 'should be a focused pill');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE);
  });

  test('it does not send a message when any other key is pressed', async function(assert) {
    assert.expect(0);

    this.set('sendMessage', () => {
      assert.ok(false);
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', XKey);

  });

  test('it sends a message when Enter is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED, 'the correct message type is sent when Enter is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER);
  });

  test('it sends a message when shiftleft is pressed', async function(assert) {
    assert.expect(1);
    const modifiers = {
      shiftKey: true
    };

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_SHIFT_LEFT_ARROW_PRESSED, 'the correct message type is sent when shift and left arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT, modifiers);
  });

  test('it sends a message when shiftRight is pressed', async function(assert) {
    assert.expect(1);
    const modifiers = {
      shiftKey: true
    };

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_SHIFT_RIGHT_ARROW_PRESSED, 'the correct message type is sent when shift and right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT, modifiers);
  });

  test('it sends a message when left is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED, 'the correct message type is sent when left arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT);
  });

  test('it sends a message when right is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED, 'the correct message type is sent when right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT);
  });

  test('it sends a message when home is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_HOME_PRESSED, 'the correct message type is sent when right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', HOME_KEY);
  });

  test('it sends a message when end is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_END_PRESSED, 'the correct message type is sent when right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', END_KEY);
  });

  test('it sends a message when ( is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_OPEN_PAREN_PRESSED, 'the correct message type is sent when `(` is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', OPEN_PAREN);
  });

  test('it sends a message when ctrl-a/A is pressed', async function(assert) {
    assert.expect(2);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_CTRL_A_PRESSED, 'the correct message type is sent when `ctrl-a` is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', UPPERCASE_A, { ctrlKey: true });
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', LOWERCASE_A, { ctrlKey: true });
  });

  test('it does not send a message when ctrl-a/A is not pressed', async function(assert) {
    assert.expect(0);

    this.set('sendMessage', (messageType) => {
      if (messageType === MESSAGE_TYPES.FOCUSED_PILL_CTRL_A_PRESSED) {
        assert.ok(false);
      }
    });
    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', UPPERCASE_A);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', LOWERCASE_A);
  });
});