import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { click, fillIn, find, findAll, focus, render, settled, triggerKeyEvent, typeIn } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_TEXT_DISABLED_LABEL,
  AFTER_OPTION_TAB_META
} from 'investigate-events/constants/pill';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';
import { toggleTab } from '../pill-util';

const { log } = console;// eslint-disable-line no-unused-vars

const ARROW_DOWN = KEY_MAP.arrowDown.key;
const ARROW_LEFT = KEY_MAP.arrowLeft.key;
const ARROW_RIGHT = KEY_MAP.arrowRight.key;
const ARROW_UP = KEY_MAP.arrowUp.key;
const BACKSPACE_KEY = KEY_MAP.backspace.key;
const ENTER_KEY = KEY_MAP.enter.key;
const ESCAPE_KEY = KEY_MAP.escape.key;
const TAB_KEY = KEY_MAP.tab.key;
const modifiers = { shiftKey: true };

const meta = { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' };
const eq = { displayName: '=', description: 'Equals', isExpensive: false, hasValue: true };
const lt = { displayName: '<', description: 'Less Than', isExpensive: false, hasValue: true };

// This trim also removes extra spaces inbetween words
const trim = (text) => text.replace(/\s+/g, ' ').trim();

module('Integration | Component | Pill Operator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('indicates it is populated when being used', async function(assert) {
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
      }}
    `);
    assert.ok(find(PILL_SELECTORS.populatedItem), 'has populated class applied to it');
  });

  test('indicates it is populated not being used but when populated with data', async function(assert) {
    this.set('selection', eq);
    await render(hbs`
      {{query-container/pill-operator
        isActive=false
        selection=selection
      }}
    `);
    assert.ok(find(PILL_SELECTORS.populatedItem), 'has populated class applied to it');
  });

  test('it shows only the value if inactive', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', eq);
    await render(hbs`
      {{query-container/pill-operator
        isActive=false
        selection=selection
      }}
    `);
    assert.equal(find(PILL_SELECTORS.operator).textContent.trim(), eq.displayName);
  });

  // There is a bug with ember-power-select-typeahead.
  // https://github.com/cibernox/ember-power-select-typeahead/issues/71
  // The workaround is to provide focus to operator after rendering it.
  test('it shows an open Power Select if active', async function(assert) {
    this.set('meta', meta);
    this.set('activePillTab', 'meta');
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        activePillTab=activePillTab
      }}
    `);
    await focus(PILL_SELECTORS.operatorTrigger);
    const options = findAll(PILL_SELECTORS.powerSelectOption);
    assert.equal(options.length, 11);
    assert.equal(trim(options[0].textContent), '= Equals');
    assert.equal(trim(options[1].textContent), '!= Does Not Equal');
    assert.equal(trim(options[2].textContent), '< Less Than');
    assert.equal(trim(options[3].textContent), '<= Less Than or Equal To');
    assert.equal(trim(options[4].textContent), '> Greater Than');
    assert.equal(trim(options[5].textContent), '>= Greater Than or Equal To');
    assert.equal(trim(options[6].textContent), 'exists Exists');
    assert.equal(trim(options[7].textContent), '!exists Does Not Exist');
    assert.equal(trim(options[8].textContent), 'contains Contains');
    assert.equal(trim(options[9].textContent), 'begins Begins');
    assert.equal(trim(options[10].textContent), 'ends Ends');
  });

  test('it broadcasts a message when a Power Select option is choosen', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        assert.deepEqual(data, eq, 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);// option "="
  });

  test('it broadcasts a message when the ARROW_LEFT key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        activePillTab=activePillTab
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_LEFT);
  });

  test('it does not broadcasts a message when the ARROW_RIGHT key is pressed and there is no selection', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        activePillTab=activePillTab
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_RIGHT);
    return settled();
  });

  test('it broadcasts a message when the ARROW_RIGHT key is pressed and there is a selection', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        this.set('selection', data);
      } else if (type === MESSAGE_TYPES.OPERATOR_ARROW_RIGHT_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        activePillTab=activePillTab
        meta=meta
        selection=selection
        sendMessage=(action handleMessage)
      }}
    `);
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_RIGHT);
  });

  test('it does not broadcasts a OPERATOR_BACKSPACE_KEY message when the BACKSPACE key is pressed mid string', async function(assert) {
    assert.expect(1);
    this.set('meta', meta);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED);
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await fillIn(PILL_SELECTORS.operatorSelectInput, 'beg');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', BACKSPACE_KEY);
    return settled();
  });

  test('it broadcasts a message when the BACKSPACE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        activePillTab=activePillTab
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', BACKSPACE_KEY);
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.OPERATOR_ESCAPE_KEY, 'Wrong message type');
      done();
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it removes the selection when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    let iteration = 1;
    assert.expect(1);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        this.set('selection', data);
        if (iteration === 2) {
          assert.equal(data, null, 'selection should be null');
          done();
        }
        iteration++;
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        selection=selection
        sendMessage=(action handleMessage)
      }}
    `);
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 1);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it selects an operator if a trailing SPACE is entered and there is one option', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        assert.deepEqual(data, eq, 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        activePillTab=activePillTab
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await typeIn(PILL_SELECTORS.operatorSelectInput, '= ');
  });

  test('it selects an operator if a trailing SPACE is entered and there are multiple options but the entered text is exactly equal to the first', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        assert.deepEqual(data, lt, 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        activePillTab=activePillTab
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await typeIn(PILL_SELECTORS.operatorSelectInput, '< ');
  });

  test('it does not select an operator if a trailing SPACE is entered and there is more than one option and that option is not exactly the text entered', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);

    document.querySelector(PILL_SELECTORS.operatorSelectInput).setAttribute('value', 'e');
    document.querySelector(PILL_SELECTORS.operatorSelectInput).setAttribute('value', ' ');
    return settled();
  });

  test('it clears out last search if Power Select looses, then gains focus', async function(assert) {
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
      }}
    `);
    await focus(PILL_SELECTORS.operatorTrigger);
    // assert number of options
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 11);
    // perform a search that down-selects the list of options
    await typeInSearch('e');
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 2); // exists and ends
    // blur and assert no options present
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ESCAPE_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 0);
    // focus and assert number of options
    await focus(PILL_SELECTORS.operatorTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 11);
  });

  test('it allows you to reselect an operator after it was previously selected', async function(assert) {
    const done = assert.async(4);
    assert.expect(4);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        assert.deepEqual(data, eq, 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    // Select via keyboard an option
    await focus(PILL_SELECTORS.operatorTrigger);
    await fillIn(PILL_SELECTORS.operatorSelectInput, '=');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
    // Reselect via keyboard the same option
    await focus(PILL_SELECTORS.operatorTrigger);
    await fillIn(PILL_SELECTORS.operatorSelectInput, '=');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
    // Select via mouse an option
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);// option "="
    // Reselect via mouse the same option
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);// option "="
  });

  test('if operator is selected (not just half entered) and you click away, leave the operator there', async function(assert) {
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        this.set('selection', data);
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        selection=selection
        sendMessage=(action handleMessage)
      }}
    `);
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);
    await blur(PILL_SELECTORS.operatorTrigger);
    assert.equal(find(PILL_SELECTORS.operatorSelectInput).value, '=');
  });

  test('it shows Advanced Options to create different types of pill', async function(assert) {
    const _hasOption = (arr, str) => arr.some((d) => d.innerText.includes(str));
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    const options = findAll(PILL_SELECTORS.powerSelectAfterOption);
    assert.equal(options.length, 2, 'incorrect number of Advanced Options');
    assert.ok(_hasOption(options, AFTER_OPTION_FREE_FORM_LABEL), 'missing option to create a free-form filter');
    assert.ok(_hasOption(options, AFTER_OPTION_TEXT_LABEL), 'missing option to create a text filter');
  });

  test('it broadcasts a message to create a free-form pill when the ENTER key is pressed', async function(assert) {
    const done = assert.async();
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, ['(foobar)', 'pill-operator'], 'correct data');
        assert.equal(find(PILL_SELECTORS.operatorSelectInput).value, '', 'meta input was reset');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    await typeInSearch('(foobar)');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
  });

  test('it does NOT broadcasts a message to create a free-form pill if no value is entered', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        log('should not be here');
        assert.notOk('should not get here');
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
  });

  test('it selects Free-Form Filter via CTRL + ↓', async function(assert) {
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    // Test flake; The after-option gets highlighted, then immediately unhighlighted
    // causing the test to fail.  double-trigger fixes this for whatever reason.
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
  });

  test('Highlight will move from operator list to Advanced Options list and back', async function(assert) {
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    // Reduce options to those that start with "e"
    await typeInSearch('e');
    // Arrow down two places
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_DOWN);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_DOWN);
    // Should be in Advanced Options now
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(trim(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent), AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
    // Arrow up
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_UP);
    // Should be back in meta options list
    assert.notOk(find(PILL_SELECTORS.powerSelectAfterOptionHighlight), 'no Advanced Options should be highlighted');
    assert.ok(find(PILL_SELECTORS.powerSelectOption), 'meta option should be highlighted');
  });

  test('it broadcasts a message to create a text pill', async function(assert) {
    const done = assert.async();
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_TEXT_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, ['foobar', 'pill-operator'], 'correct data');
        assert.equal(find(PILL_SELECTORS.operatorSelectInput).value, '', 'meta input was reset');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    await typeInSearch('foobar');
    // trigger Text Filter option
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_LABEL));
    assert.ok(textFilter, 'unable to find Text Filter option');
    await click(textFilter);
  });

  test('it renders a disabled option for text filters when there is a text filter already', async function(assert) {
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        hasTextPill=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    // trigger Text Filter option
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_DISABLED_LABEL));
    assert.ok(textFilter, 'unable to find Text Filter option');
  });

  test('it broadcasts a message to toggle tabs via pill operator', async function(assert) {
    assert.expect(3);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED) {
        assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'Correct message sent up');
        assert.deepEqual(data, { data: 'foobar', dataSource: 'pill-operator' }, 'Correct data sent up');
      } else {
        assert.equal(type, MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED);
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    await typeInSearch('foobar');

    await click(PILL_SELECTORS.recentQueriesTab);
  });

  test('it broadcasts a message to toggle tabs when tab or shiftTab is pressed via pill operator', async function(assert) {
    assert.expect(2);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'Correct message sent up');
    });
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);

    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', TAB_KEY);

    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', TAB_KEY, modifiers);
  });

  test('it highlights proper Advanced Option if all EPS options filtered out', async function(assert) {
    let option;
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    // Type in text
    await typeInSearch('x');
    option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_TEXT_LABEL), 'Text Filter was not highlighted');
    // Reset
    await typeInSearch('');
    // Type in complex text
    await typeInSearch('(x)');
    option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_FREE_FORM_LABEL), 'Free-Form Filter was not highlighted');
  });

  test('it does not broadcast a messaage to toggle if a pill is opened in edit state', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'This should not be triggered');
    });
    await render(hbs`
      {{query-container/pill-operator
        isEditing=true
        isActive=true
        meta=meta
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
  });

  test('it broadcasts a message when operator is backspaced length 0, also ensures recent queries are fetched', async function(assert) {
    assert.expect(6);
    this.set('meta', meta);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    let count = 0;
    this.set('handleMessage', (type, data) => {

      if (count === 0) {
        assert.equal(type, MESSAGE_TYPES.OPERATOR_SELECTED, 'correct message sent up');
        assert.deepEqual(data, eq, 'Wrong message data');
        count++;
      } else if (count === 1) {
        assert.equal(type, MESSAGE_TYPES.OPERATOR_SELECTED, 'correct message sent up');
        assert.equal(data, null, 'Should be no data being sent up');
        count++;
      } else {
        assert.equal(type, MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED, 'correct message sent up');
        assert.deepEqual(data, { data: '', dataSource: 'pill-operator' }, 'Correct data sent up');
      }
    });
    await render(hbs`
      {{query-container/pill-operator
        activePillTab=activePillTab
        isActive=true
        meta=meta
        sendMessage=(action handleMessage)
      }}
    `);
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0); // `=`
    await typeInSearch('');
    return settled();
  });

  test('it disables Text Filter if not supported by core services', async function(assert) {
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        canPerformTextSearch=false
        isActive=true
        meta=meta
      }}
    `);
    await clickTrigger(PILL_SELECTORS.operator);
    // Reduce options so that no Query Filter options are left
    await typeInSearch('foo');
    // Highlight should be in Advanced Options on Free-Form
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(trim(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent), AFTER_OPTION_FREE_FORM_LABEL, 'Free-Form option was not highlighted');
    // Text Filter should be disabled with a message stating reason
    const advancedOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    assert.equal(advancedOptions.length, 2, 'incorrect number of Advanced Options present');
    assert.equal(trim(advancedOptions[1].textContent), 'Text Filter is unavailable. All services must be 11.3 or greater.', 'incorrect label for Text Filter option');
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionDisabled).length, 1, 'incorrect number of disabled items');
  });
});