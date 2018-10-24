import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { fillIn, find, findAll, focus, render, settled, triggerKeyEvent } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';

// const { log } = console;

const ARROW_LEFT = KEY_MAP.arrowLeft.code;
const ARROW_RIGHT = KEY_MAP.arrowRight.code;
const BACKSPACE_KEY = KEY_MAP.backspace.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const TAB_KEY = KEY_MAP.tab.code;

const trim = (text) => text.replace(/\s+/g, '').trim();
const meta = { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' };
const eq = { displayName: '=', isExpensive: false, hasValue: true };

module('Integration | Component | Pill Operator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
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
    assert.equal(trim(find(PILL_SELECTORS.operator).textContent), eq.displayName);
  });

  // There is a bug with ember-power-select-typeahead.
  // https://github.com/cibernox/ember-power-select-typeahead/issues/71
  // The workaround is to provide focus to operator after rendering it.
  test('it shows an open Power Select if active', async function(assert) {
    this.set('meta', meta);
    await render(hbs`
      {{query-container/pill-operator
        isActive=true
        meta=meta
      }}
    `);
    await focus(PILL_SELECTORS.operatorTrigger);
    const options = findAll(PILL_SELECTORS.powerSelectOption);
    assert.equal(options.length, 7);
    assert.equal(options[0].textContent.trim(), '=');
    assert.equal(options[1].textContent.trim(), '!=');
    assert.equal(options[2].textContent.trim(), 'exists');
    assert.equal(options[3].textContent.trim(), '!exists');
    assert.equal(options[4].textContent.trim(), 'contains');
    assert.equal(options[5].textContent.trim(), 'begins');
    assert.equal(options[6].textContent.trim(), 'ends');
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
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.OPERATOR_ARROW_LEFT_KEY) {
        assert.ok('message dispatched');
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
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_LEFT);
  });

  test('it does not broadcasts a message when the ARROW_RIGHT key is pressed and there is no selection', async function(assert) {
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
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_RIGHT);
    return settled();
  });

  test('it broadcasts a message when the ARROW_RIGHT key is pressed and there is a selection', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
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
        meta=meta
        selection=selection
        sendMessage=(action handleMessage)
      }}
    `);
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ARROW_RIGHT);
  });

  test('it does not broadcasts a message when the BACKSPACE key is pressed mid string', async function(assert) {
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
    await fillIn(PILL_SELECTORS.operatorSelectInput, 'beg');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', BACKSPACE_KEY);
    return settled();
  });

  test('it broadcasts a message when the BACKSPACE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.OPERATOR_BACKSPACE_KEY) {
        assert.ok('message dispatched');
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
    // We go back to old-skool jQuery for this because fillIn() performs a focus
    // event on the input every time you call it which causes the search to
    // clear out. PowerSelect test helper typeInSearch() ends up just calling
    // fillIn(). Also, fillIn() doesn't seem to properly trigger an InputEvent,
    // so the input handler doesn't get a down-selected list of meta options.
    this.$(PILL_SELECTORS.operatorSelectInput).val('=').trigger('input');
    this.$(PILL_SELECTORS.operatorSelectInput).val(' ').trigger('input');
  });

  test('it does not select an operator if a trailing SPACE is entered and there is more than one option', async function(assert) {
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
    this.$(PILL_SELECTORS.operatorSelectInput).val('e').trigger('input');
    this.$(PILL_SELECTORS.operatorSelectInput).val(' ').trigger('input');
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
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 7);
    // perform a search that down-selects the list of options
    await typeInSearch('e');
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 2); // exists and ends
    // blur and assert no options present
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', TAB_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 0);
    // focus and assert number of options
    await focus(PILL_SELECTORS.operatorTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 7);
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

  test('it broadcasts a message to create a free-form pill when the ENTER key is pressed', async function(assert) {
    const done = assert.async();
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, ['foobar', 'operator'], 'correct data');
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
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
  });

  test('it does NOT broadcasts a message to create a free-form pill if no value is entered', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
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
});