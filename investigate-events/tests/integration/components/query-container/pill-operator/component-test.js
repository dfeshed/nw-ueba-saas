import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { find, findAll, focus, render, settled, triggerKeyEvent } from '@ember/test-helpers';

const TAB_KEY = 9;

const operator = '.pill-operator';
const operatorPowerSelectTrigger = '.pill-operator .ember-power-select-trigger';
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
    assert.expect(2);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type == 'PILL::OPERATOR_CLICKED') {
        return; // don't care about click events
      }
      assert.equal(type, 'PILL::OPERATOR_SELECTED', 'Wrong message type');
      assert.deepEqual(data, eq, 'Wrong message data');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await focus(operatorPowerSelectTrigger);
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
    return settled();
  });

  test('it selects an operator if a trailing SPACE is entered and there is one option', async function(assert) {
    assert.expect(2);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      assert.equal(type, 'PILL::OPERATOR_SELECTED', 'Wrong message type');
      assert.deepEqual(data, eq, 'Wrong message data');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await focus(operatorPowerSelectTrigger);
    // We go back to old-skool jQuery for this because fillIn() performs a focus
    // event on the input every time you call it which causes the search to
    // clear out. PowerSelect test helper typeInSearch() ends up just calling
    // fillIn(). Also, fillIn() doesn't seem to properly trigger an InputEvent,
    // so the input handler doesn't get a down-selected list of meta options.
    this.$('input').val('=').trigger('input');
    this.$('input').val(' ').trigger('input');
    return settled();
  });

  test('it does not select an operator if a trailing SPACE is entered and there is more than one option', async function(assert) {
    assert.expect(0);
    this.set('meta', meta);
    this.set('handleMessage', () => {
      assert.notOk('The sendMessage handler was erroneously invoked');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await focus(operatorPowerSelectTrigger);
    this.$('input').val('e').trigger('input');
    this.$('input').val(' ').trigger('input');
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
    await triggerKeyEvent(operatorPowerSelectTrigger, 'keydown', TAB_KEY);
    assert.equal(findAll(powerSelectOption).length, 0);
    // focus and assert number of options
    await focus(operatorPowerSelectTrigger);
    assert.equal(findAll(powerSelectOption).length, 7);
  });

  test('it allows you to reselect an operator after it was previously selected', async function(assert) {
    assert.expect(4);
    this.set('meta', meta);
    this.set('handleMessage', (type, data) => {
      if (type == 'PILL::OPERATOR_CLICKED') {
        return; // don't care about click events
      }
      assert.equal(type, 'PILL::OPERATOR_SELECTED', 'Wrong message type');
      assert.deepEqual(data, eq, 'Wrong message data');
    });
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta sendMessage=(action handleMessage)}}`);
    await focus(operatorPowerSelectTrigger);
    // Select an option
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
    // Reselect the same option
    await selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
  });
});