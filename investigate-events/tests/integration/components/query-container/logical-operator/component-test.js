import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { find, render, triggerKeyEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import PILL_SELECTORS from '../pill-selectors';
import { OPERATOR_AND, OPERATOR_OR } from 'investigate-events/constants/pill';
import { createOperator } from 'investigate-events/util/query-parsing';
import KEY_MAP from 'investigate-events/util/keys';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';

const LeftArrowKey = KEY_MAP.arrowLeft.key;
const RightArrowKey = KEY_MAP.arrowRight.key;
// const DeleteKey = KEY_MAP.delete.key;

module('Integration | Component | Logical Operator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it renders as an AND by default', async function(assert) {
    await render(hbs`
      {{query-container/logical-operator}}
    `);
    assert.ok(find(PILL_SELECTORS.logicalOperatorAND), 'renders as AND by default');
  });

  test('it renders as an AND', async function(assert) {
    this.set('pillData', createOperator(OPERATOR_AND));
    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.logicalOperatorAND), 'renders as AND');
  });

  test('it renders as an OR', async function(assert) {
    this.set('pillData', createOperator(OPERATOR_OR));
    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.logicalOperatorOR), 'renders as OR');
  });

  test('it sends a message when focused and left arrow is pressed', async function(assert) {
    assert.expect(3);

    this.set('sendMessage', (messageType, position) => {
      assert.equal(
        messageType,
        MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT,
        'the correct message type is sent when left is pressed'
      );
      assert.equal(position, 2, 'position is passed');
    });
    this.set('pillData', {
      ...createOperator(OPERATOR_AND),
      isFocused: true
    });

    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
        sendMessage=sendMessage
        position=2
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
        'the correct message type is sent when right is pressed'
      );
      assert.equal(position, 2, 'position is passed');
    });
    this.set('pillData', {
      ...createOperator(OPERATOR_AND),
      isFocused: true
    });

    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
        sendMessage=sendMessage
        position=2
      }}
    `);

    assert.ok(find(PILL_SELECTORS.focusedPill), 'the pill is focused');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', RightArrowKey);
  });

  test('if not focused, style is not applied and focus holder is not present', async function(assert) {
    assert.expect(2);

    this.set('pillData', {
      ...createOperator(OPERATOR_AND),
      isFocused: false
    });

    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
      }}
    `);

    assert.notOk(find(PILL_SELECTORS.focusedPill), 'the pill is not focused');
    assert.notOk(find(PILL_SELECTORS.focusHolderInput), 'should be no focus holder to accept keystrokes');
  });

  test('if focused, style is applied and focus holder is present', async function(assert) {
    assert.expect(2);

    this.set('pillData', {
      ...createOperator(OPERATOR_AND),
      isFocused: true
    });

    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
      }}
    `);

    assert.ok(find(PILL_SELECTORS.focusedPill), 'the pill is not focused');
    assert.ok(find(PILL_SELECTORS.focusHolderInput), 'should be no focus holder to accept keystrokes');
  });

});