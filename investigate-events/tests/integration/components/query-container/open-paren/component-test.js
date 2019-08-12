import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';

import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';

const LeftArrowKey = KEY_MAP.arrowLeft.key;
const RightArrowKey = KEY_MAP.arrowRight.key;
const DeleteKey = KEY_MAP.delete.key;

const { log } = console;// eslint-disable-line no-unused-vars

module('Integration | Component | Open Paren', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    await render(hbs`
      {{query-container/open-paren}}
    `);
    assert.equal(find(PILL_SELECTORS.openParen).textContent.trim(), '(');
  });

  test('contains proper class when twin is focused', async function(assert) {
    this.set('pillData', {
      isTwinFocused: true
    });
    await render(hbs`
      {{query-container/open-paren
        pillData=pillData
      }}
    `);

    const twinFocused = findAll(PILL_SELECTORS.openParenTwinFocused);
    assert.equal(twinFocused.length, 1, 'twin focused not present');
  });

  test('it sends a message when focused and left arrow is pressed', async function(assert) {
    assert.expect(3);

    this.set('sendMessage', (messageType, position) => {
      assert.equal(
        messageType,
        MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT,
        'the correct message type is sent when left is pressed'
      );
      assert.equal(position, 1, 'position is passed');
    });
    this.set('position', 1);
    this.set('pillData', {
      isFocused: true
    });

    await render(hbs`
      {{query-container/open-paren
        sendMessage=sendMessage
        pillData=pillData
        position=position
      }}
    `);

    assert.ok(find(PILL_SELECTORS.focusedPill), 'the pill is focused');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', LeftArrowKey);
  });

  test('it sends a message when focused and right arrow is pressed', async function(assert) {
    assert.expect(3);

    this.set('sendMessage', (messageType, position) => {
      assert.equal(
        messageType,
        MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT,
        'the correct message type is sent when left is pressed'
      );
      assert.equal(position, 7, 'position is passed');
    });
    this.set('position', 7);
    this.set('pillData', {
      isFocused: true
    });

    await render(hbs`
      {{query-container/open-paren
        sendMessage=sendMessage
        pillData=pillData
        position=position
      }}
    `);

    assert.ok(find(PILL_SELECTORS.focusedPill), 'the pill is focused');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', RightArrowKey);
  });

  test('if not focused, style is not applied and focus holder is not present', async function(assert) {
    assert.expect(2);

    this.set('sendMessage', () => {});
    this.set('position', 7);
    this.set('pillData', {
      isFocused: false
    });

    await render(hbs`
      {{query-container/open-paren
        sendMessage=sendMessage
        pillData=pillData
        position=position
      }}
    `);

    assert.notOk(find(PILL_SELECTORS.focusedPill), 'the pill is not focused');
    assert.notOk(find(PILL_SELECTORS.focusHolderInput), 'should be no focus holder to accept keystrokes');
  });

  test('clicking a open paren sends message to select/deselect', async function(assert) {
    assert.expect(4);

    let selected = false;

    this.set('sendMessage', (messageType) => {
      if (selected) {
        assert.equal(
          messageType,
          MESSAGE_TYPES.PILL_DESELECTED,
          'the correct message type is sent when close paren clicked'
        );
        assert.ok(find(PILL_SELECTORS.selectedPill), 'the pill is selected');
        selected = true;
      } else {
        assert.equal(
          messageType,
          MESSAGE_TYPES.PILL_SELECTED,
          'the correct message type is sent when close paren clicked'
        );
        assert.notOk(find(PILL_SELECTORS.selectedPill), 'the pill is not selected');
      }
    });

    this.set('position', 7);
    this.set('pillData', {
      isFocused: false
    });

    await render(hbs`
      {{query-container/open-paren
        sendMessage=sendMessage
        pillData=pillData
        position=position
      }}
    `);

    await click(PILL_SELECTORS.openParen);

    await click(PILL_SELECTORS.openParen);
  });

  test('it sends a message when delete is pressed', async function(assert) {
    assert.expect(3);

    this.set('sendMessage', (messageType, pillData) => {
      assert.equal(
        messageType,
        MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL,
        'the correct message type is sent when delete is pressed'
      );
      assert.equal(pillData.id, 12345, 'pill data is passed');
    });
    this.set('position', 7);
    this.set('pillData', {
      id: 12345,
      isFocused: true
    });

    await render(hbs`
      {{query-container/open-paren
        sendMessage=sendMessage
        pillData=pillData
        position=position
      }}
    `);

    assert.ok(find(PILL_SELECTORS.focusedPill), 'the pill is focused');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DeleteKey);
  });

});
