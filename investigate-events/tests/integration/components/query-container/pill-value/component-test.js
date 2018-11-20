import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { click, find, focus, render, triggerKeyEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';

const BACKSPACE_KEY = KEY_MAP.backspace.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const LEFT_ARROW_KEY = KEY_MAP.arrowLeft.code;

const { log } = console;// eslint-disable-line no-unused-vars

module('Integration | Component | Pill Value', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('indicates it is populated when being used', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=true
      }}
    `);
    assert.ok(find(PILL_SELECTORS.populatedItem), 'has populated class applied to it');
  });

  test('indicates it is populated when not being used, but has data', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=false
        valueString="'foo'"
      }}
    `);
    assert.ok(find(PILL_SELECTORS.populatedItem), 'has populated class applied to it');
  });

  test('it broadcasts a CLICKED event when clicked upon and is inactive', async function(assert) {
    const done = assert.async(1);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_CLICKED) {
        assert.ok('message dispatched');
        // Should only hit this once
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=false
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.value);

    // Again, this time "active"
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.value);
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
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', LEFT_ARROW_KEY);
  });

  test('it does not broadcasts a message when the BACKSPACE key is pressed and there is a value', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_BACKSPACE_KEY) {
        assert.notOk('message dispatched');
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
        valueString="'xx'"
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', BACKSPACE_KEY);
    // return settled();
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
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', BACKSPACE_KEY);
  });

  test('it does not broadcasts a message when the ENTER key is pressed and there is no value', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', (type) => {
      assert.notOk(type);
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
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
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
        valueString='x'
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message when the ENTER key is pressed and there is a quoted value', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.VALUE_ENTER_KEY) {
        assert.equal(data, 'x', 'Wrong input string');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
        valueString="'x'"
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
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
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it removes all text when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', async (type) => {
      if (type === MESSAGE_TYPES.VALUE_ESCAPE_KEY) {
        assert.equal(find(PILL_SELECTORS.valueSelectInput).textContent, '', 'input should be empty');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
        valueString='\\'foo\\''
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it trims input before broadcasting a message', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.VALUE_ENTER_KEY) {
        assert.equal(data, 'x', 'Wrong input string');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
        valueString='  x  '
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('if there is a quoted value, it trims off any space, before broadcasting a message', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.VALUE_ENTER_KEY) {
        assert.equal(data, 'x', 'Wrong input string');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
        valueString="'x  '"
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message to create a free-form pill when the FREE-FORM option is selected', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, ['foobar', 'pill-value'], 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    await typeInSearch('foobar');
    await selectChoose(PILL_SELECTORS.valueTrigger, PILL_SELECTORS.powerSelectOption, 1); // Free-Form
  });
});