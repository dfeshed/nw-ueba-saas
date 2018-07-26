import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, triggerKeyEvent } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const DELETE_KEY = KEY_MAP.delete.code;
const BACKSPACE = KEY_MAP.backspace.code;
const X_KEY = 88;

module('Integration | Component | focus-holder', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it sends a message when delete is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.SELECTED_FOCUS_DELETE_PRESSED, 'the correct message type is sent when delete is pressed');
    });

    await render(hbs`{{query-container/focus-holder sendMessage=sendMessage}}`);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
  });

  test('it sends a message when backspace is pressed', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.SELECTED_FOCUS_DELETE_PRESSED, 'the correct message type is sent when backspace is pressed');
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
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', X_KEY);

  });
});