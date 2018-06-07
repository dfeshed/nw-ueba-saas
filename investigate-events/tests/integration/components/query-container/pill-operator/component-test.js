import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { fillIn, find, findAll, focus, render, settled, triggerKeyEvent } from '@ember/test-helpers';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';

// const { log } = console;

const TAB_KEY = KEY_MAP.tab.code;
const ARROW_LEFT = KEY_MAP.arrowLeft.code;
const ARROW_RIGHT = KEY_MAP.arrowRight.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const BACKSPACE_KEY = KEY_MAP.backspace.code;

const operator = '.pill-operator';
const operatorPowerSelectTrigger = '.pill-operator .ember-power-select-trigger';
const operatorPowerSelectInput = '.pill-operator .ember-power-select-trigger input';
const powerSelectOption = '.ember-power-select-option';
const trim = (text) => text.replace(/\s+/g, '').trim();
const meta = { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' };
const eq = { displayName: '=', isExpensive: false, hasValue: true };

module('Integration | Component | Pill Operator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it shows only the value if inactive', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', eq);
    await render(hbs`{{query-container/pill-operator isActive=false selection=selection}}`);
    assert.equal(trim(find(operator).textContent), eq.displayName);
  });

  // There is a bug with ember-power-select-typeahead.
  // https://github.com/cibernox/ember-power-select-typeahead/issues/71
  // The workaround is to provide focus to operator after rendering it.
  test('it shows an open Power Select if active', async function(assert) {
    this.set('meta', meta);
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta}}`);
    await focus(operatorPowerSelectTrigger);
    const options = findAll(powerSelectOption);
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
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
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
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', ARROW_LEFT);
  });

  test('it does not broadcasts a message when the ARROW_RIGHT key is pressed and there is no selection', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', ARROW_RIGHT);
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
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta selection=selection sendMessage=(action handleMessage)}}`);
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', ARROW_RIGHT);
  });

  test('it does not broadcasts a message when the BACKSPACE key is pressed mid string', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await fillIn(operatorPowerSelectInput, 'beg');
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', BACKSPACE_KEY);
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
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', BACKSPACE_KEY);
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('meta', meta);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.OPERATOR_ESCAPE_KEY, 'Wrong message type');
      done();
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', ESCAPE_KEY);
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
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    // We go back to old-skool jQuery for this because fillIn() performs a focus
    // event on the input every time you call it which causes the search to
    // clear out. PowerSelect test helper typeInSearch() ends up just calling
    // fillIn(). Also, fillIn() doesn't seem to properly trigger an InputEvent,
    // so the input handler doesn't get a down-selected list of meta options.
    this.$(operatorPowerSelectInput).val('=').trigger('input');
    this.$(operatorPowerSelectInput).val(' ').trigger('input');
  });

  test('it does not select an operator if a trailing SPACE is entered and there is more than one option', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    this.$(operatorPowerSelectInput).val('e').trigger('input');
    this.$(operatorPowerSelectInput).val(' ').trigger('input');
    return settled();
  });

  test('it clears out last search if Power Select looses, then gains focus', async function(assert) {
    this.set('meta', meta);
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta}}`);
    await focus(operatorPowerSelectTrigger);
    // assert number of options
    assert.equal(findAll(powerSelectOption).length, 7);
    // perform a search that down-selects the list of options
    await typeInSearch('e');
    assert.equal(findAll(powerSelectOption).length, 2); // exists and ends
    // blur and assert no options present
    await triggerKeyEvent(operatorPowerSelectInput, 'keydown', TAB_KEY);
    assert.equal(findAll(powerSelectOption).length, 0);
    // focus and assert number of options
    await focus(operatorPowerSelectTrigger);
    assert.equal(findAll(powerSelectOption).length, 7);
  });

  test('it allows you to reselect an operator after it was previously selected', async function(assert) {
    const done = assert.async();
    let iterations = 0;
    assert.expect(2);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.OPERATOR_SELECTED) {
        assert.deepEqual(data, eq, 'Wrong message data');
        iterations++;
      }
      if (iterations === 2) {
        done();
      }
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    // Select an option
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
    // Reselect the same option
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
  });
});