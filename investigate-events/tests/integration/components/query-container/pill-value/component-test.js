import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { fillIn, render, settled, triggerKeyEvent } from '@ember/test-helpers';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';

const BACKSPACE_KEY = KEY_MAP.backspace.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const LEFT_ARROW_KEY = KEY_MAP.arrowLeft.code;
const X_KEY = 88;

// const { log } = console;

module('Integration | Component | Pill Value', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it broadcasts a message when the ARROW_LEFT key pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', LEFT_ARROW_KEY);
    // await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keyup', LEFT_ARROW_KEY);
  });

  test('it does not broadcasts a message when the BACKSPACE key is pressed and there is a value', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_BACKSPACE_KEY) {
        assert.notOk('message dispatched');
      }
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await fillIn(PILL_SELECTORS.valueInput, 'xx');
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', BACKSPACE_KEY);
    // await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keyup', BACKSPACE_KEY);
    return settled();
  });

  test('it broadcasts a message when the BACKSPACE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_BACKSPACE_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', BACKSPACE_KEY);
    // await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keyup', BACKSPACE_KEY);
  });

  test('it does not broadcasts a message when the ENTER key is pressed and there is no value', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ENTER_KEY);
    return settled();
  });

  test('it broadcasts a message when the ENTER key is pressed and there is a value', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.VALUE_ENTER_KEY) {
        assert.equal(data, 'x', 'Wrong input string');
        done();
      }
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await fillIn(PILL_SELECTORS.valueInput, 'x');
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', X_KEY);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_ESCAPE_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ESCAPE_KEY);
  });
});