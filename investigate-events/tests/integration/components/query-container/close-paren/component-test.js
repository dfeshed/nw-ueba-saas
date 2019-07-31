import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { find, render, triggerKeyEvent } from '@ember/test-helpers';

import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';

const { log } = console;// eslint-disable-line no-unused-vars

const LeftArrowKey = KEY_MAP.arrowLeft.key;
const RightArrowKey = KEY_MAP.arrowRight.key;

module('Integration | Component | Close Paren', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    await render(hbs`
      {{query-container/close-paren}}
    `);
    assert.equal(find(PILL_SELECTORS.closeParen).textContent.trim(), ')');
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
      {{query-container/close-paren
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
      {{query-container/close-paren
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
      {{query-container/close-paren
        sendMessage=sendMessage
        pillData=pillData
        position=position
      }}
    `);

    assert.notOk(find(PILL_SELECTORS.focusedPill), 'the pill is not focused');
    assert.notOk(find(PILL_SELECTORS.focusHolderInput), 'should be no focus holder to accept keystrokes');
  });
});
