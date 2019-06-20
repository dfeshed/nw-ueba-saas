import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { click, find, findAll, focus, render, triggerKeyEvent } from '@ember/test-helpers';
import { clickTrigger, typeInSearch } from 'ember-power-select/test-support/helpers';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_QUERY_LABEL,
  AFTER_OPTION_TEXT_DISABLED_LABEL,
  AFTER_OPTION_TAB_META
} from 'investigate-events/constants/pill';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';
import { toggleTab } from '../pill-util';

const BACKSPACE_KEY = KEY_MAP.backspace.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const LEFT_ARROW_KEY = KEY_MAP.arrowLeft.code;
const ARROW_DOWN = KEY_MAP.arrowDown.code;
const TAB_KEY = KEY_MAP.tab.code;
const modifiers = { shiftKey: true };

const { log } = console;// eslint-disable-line no-unused-vars

module('Integration | Component | Pill Value', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
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
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        activePillTab=activePillTab
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
  });

  test('it broadcasts a message when the BACKSPACE key is pressed, deleting the last character', async function(assert) {
    // The handleMessage callback gets called twice because, in the  test env,
    // the onBlur get's called immediately after the onKeyDown. This causes two
    // `VALUE_SET` events dispatched back to back.
    // onBur is now guarded by a condition which checks if the text being sent out is already set as _searchString.
    assert.expect(1);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_SET) {
        assert.ok(true, 'This should be called');
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
        valueString='x'
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', BACKSPACE_KEY);
  });

  test('it broadcasts a message when the BACKSPACE key is pressed, and there are no characters', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.VALUE_BACKSPACE_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        activePillTab=activePillTab
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
    this.set('handleMessage', async(type) => {
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
    this.set('activePillTab', AFTER_OPTION_TAB_META);
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
        activePillTab=activePillTab
      }}
    `);
    await focus(PILL_SELECTORS.valueTrigger);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('it shows Advanced Options to create different types of pill', async function(assert) {
    const _hasOption = (arr, str) => arr.some((d) => d.innerText.includes(str));
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        activePillTab=activePillTab
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    const options = findAll(PILL_SELECTORS.powerSelectOption);
    assert.equal(options.length, 1, 'incorrect number of options');
    assert.ok(_hasOption(options, AFTER_OPTION_QUERY_LABEL), 'incorrect option to create a query filter');
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    assert.equal(afterOptions.length, 2, 'incorrect number of options');
    assert.ok(_hasOption(afterOptions, AFTER_OPTION_FREE_FORM_LABEL), 'incorrect option to create a free-form filter');
    assert.ok(_hasOption(afterOptions, AFTER_OPTION_TEXT_LABEL), 'incorrect option to create a text filter');
  });

  test('it broadcasts a message to create a free-form pill when the Free-Form Filter option is selected', async function(assert) {
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
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const freeFormFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_FREE_FORM_LABEL));
    assert.ok(freeFormFilter, 'unable to find Free-Form Filter option');
    await click(freeFormFilter);
  });

  test('it broadcasts a message to create a free-form pill when the Free-Form Filter option is selected and hit ENTER', async function(assert) {
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
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    const text = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.split(/\n/g).map((s) => s.trim()).join('');
    assert.equal(text, AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message to create a text pill when the Text Filter option is selected and hit ENTER', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_TEXT_PILL) {
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
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    const text = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.split(/\n/g).map((s) => s.trim()).join('');
    assert.equal(text, AFTER_OPTION_TEXT_LABEL, 'second Advanced Option was not highlighted');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message to create a text pill when the Text Filter option is selected', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_TEXT_PILL) {
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
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_LABEL));
    assert.ok(textFilter, 'unable to find Text Filter option');
    await click(textFilter);
  });

  test('if there are quoted strings within a complex pill, do not remove the outer quotes', async function(assert) {
    const done = assert.async();
    const inputString = "'GET' || action = 'PUT'";
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, [inputString, 'pill-value'], 'correct data');
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
    await typeInSearch(inputString);
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const freeFormFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_FREE_FORM_LABEL));
    assert.ok(freeFormFilter, 'unable to find Free-Form Filter option');
    await click(freeFormFilter);
  });

  test('it selects Free-Form Filter via CTRL + ↓', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
  });

  test('it selects Text Filter via CTRL + ↓', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_TEXT_LABEL, 'second Advanced Option was not highlighted');
  });

  test('it renders a disabled option for text filters when there is a text filter already', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        hasTextPill=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    // trigger Text Filter option
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_DISABLED_LABEL));
    assert.ok(textFilter, 'unable to find Text Filter option');
  });

  test('it selects Free-Form Filter via ↓', async function(assert) {
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    await render(hbs`
      {{query-container/pill-value
        activePillTab=activePillTab
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    assert.notOk(find(PILL_SELECTORS.powerSelectAfterOptionHighlight), 'No Advanced Options should be highlighted');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN);
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
  });

  test('it selects Text Filter via ↓', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    assert.notOk(find(PILL_SELECTORS.powerSelectAfterOptionHighlight), 'No Advanced Options should be highlighted');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN);
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_TEXT_LABEL, 'second Advanced Option was not highlighted');
  });

  test('it broadcasts a message to toggle tabs via pill value', async function(assert) {
    assert.expect(1);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'Correct message sent up');
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);

    await click(PILL_SELECTORS.recentQueriesTab);
  });

  test('it broadcasts a message to toggle tabs when tab or shift is pressed via pill value', async function(assert) {
    assert.expect(2);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'Correct message sent up');
    });
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);

    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', TAB_KEY);

    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', TAB_KEY, modifiers);
  });

  test('it highlights proper EPS option depending upon text entered', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        isActive=true
        meta=meta
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    // Type in text
    await typeInSearch('x');
    assert.notOk(find(PILL_SELECTORS.powerSelectAfterOptionHighlight), 'Advanced Options should not be highlighted');
    // Reset
    await typeInSearch('');
    // Type in complex text
    await typeInSearch('(x)');
    const option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_FREE_FORM_LABEL), 'Free-Form Filter was not highlighted');
  });

  test('it does not broadcast a message to toggle tabs when a pill is open for edit', async function(assert) {
    assert.expect(0);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'Correct message sent up');
    });
    await render(hbs`
      {{query-container/pill-value
        isEditing=true
        isActive=true
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);

    await toggleTab(PILL_SELECTORS.valueSelectInput);
  });

  test('it disables Text Filter if not supported by core services', async function(assert) {
    await render(hbs`
      {{query-container/pill-value
        canPerformTextSearch=false
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.value);
    // Reduce options so that no Query Filter options are left
    await typeInSearch('foo');
    // Text Filter should be disabled with a message stating reason
    const advancedOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    assert.equal(advancedOptions.length, 2, 'incorrect number of Advanced Options present');
    assert.equal(advancedOptions[1].textContent.trim(), 'Text Filter is unavailable. All services must be 11.3 or greater.', 'incorrect label for Text Filter option');
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionDisabled).length, 1, 'incorrect number of disabled items');
  });
});