import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, triggerKeyEvent } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const LeftArrowKey = KEY_MAP.arrowLeft.key;
const RightArrowKey = KEY_MAP.arrowRight.key;
const Backspace = KEY_MAP.backspace.key;
const DeleteKey = KEY_MAP.delete.key;
const Enter = KEY_MAP.enter.key;
const XKey = 'KeyX';
const HomeKey = KEY_MAP.home.key;
const EndKey = KEY_MAP.end.key;

module('Integration | Component | focus-holder', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it sends a message when delete is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_DELETE_PRESSED, 'the correct message type is sent when delete is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DeleteKey);
  });

  test('it sends a message when Backspace is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_DELETE_PRESSED, 'the correct message type is sent when Backspace is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', Backspace);
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
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', Enter);
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
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', LeftArrowKey, modifiers);
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
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', RightArrowKey, modifiers);
  });

  test('it sends a message when left is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED, 'the correct message type is sent when left arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', LeftArrowKey);
  });

  test('it sends a message when right is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED, 'the correct message type is sent when right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', RightArrowKey);
  });

  test('it sends a message when home is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_HOME_PRESSED, 'the correct message type is sent when right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', HomeKey);
  });

  test('it sends a message when end is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_END_PRESSED, 'the correct message type is sent when right arrow is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', EndKey);
  });
});