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
});