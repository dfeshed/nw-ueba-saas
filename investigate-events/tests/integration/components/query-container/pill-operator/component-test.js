import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { find, findAll, render, settled } from '@ember/test-helpers';

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

  // This test is skipped due to a ember-power-select-typeahead bug
  // https://github.com/cibernox/ember-power-select-typeahead/issues/71
  skip('it shows an open Power Select if active', async function(assert) {
    this.set('meta', meta);
    await render(hbs`{{query-container/pill-operator isActive=true meta=meta}}`);
    assert.equal(findAll(powerSelectOption).length, 7);
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
    selectChoose(operatorPowerSelectTrigger, powerSelectOption, 0);// option "="
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
    this.$('input').val('e').trigger('input');
    this.$('input').val(' ').trigger('input');
    return settled();
  });
});