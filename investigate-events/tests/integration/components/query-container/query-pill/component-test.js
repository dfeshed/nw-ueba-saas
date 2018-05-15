import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { click, fillIn, find, findAll, focus, render, waitUntil } from '@ember/test-helpers';

// const { log } = console;

const meta = '.pill-meta';
const metaPowerSelect = '.pill-meta .ember-power-select-trigger';
const metaInput = '.pill-meta input';
const operator = '.pill-operator';
const operatorPowerSelect = '.pill-operator .ember-power-select-trigger';
const powerSelectOption = '.ember-power-select-option';
const valueInput = '.pill-value input';
const trim = (text) => text.replace(/\s+/g, '').trim();

const initialState = {
  language: [
    { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' },
    { count: 0, format: 'Text', metaName: 'b', flags: 2, displayName: 'B' },
    { count: 0, format: 'Text', metaName: 'c', flags: 3, displayName: 'C' }
  ]
};

let setState;

module('Integration | Component | Query Pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { investigate: { dictionaries: state } };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  test('it sends a message that it was initialized', async function(assert) {
    this.set('handleMessage', (message) => {
      assert.equal(message, 'PILL::INITIALIZED', 'Initalization message does not match');
    });
    await render(hbs`{{query-container/query-pill sendMessage=(action handleMessage)}}`);
  });

  test('it activates pill-meta if active upon initialization', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    assert.equal(findAll(metaPowerSelect).length, 1);
  });

  test('it allows you to select a meta value', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(metaPowerSelect, powerSelectOption, 0);// option a
    await waitUntil(() => !find(metaPowerSelect));
    assert.equal(trim(find(meta).textContent), 'a');
  });

  test('it allows you to select an operator after a meta value was selected', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(metaPowerSelect, powerSelectOption, 0);// option A
    await waitUntil(() => find(operatorPowerSelect));
    selectChoose(operatorPowerSelect, powerSelectOption, 0);// option =
    await waitUntil(() => find(operator));
    assert.equal(trim(find(operator).textContent), '=');
  });

  test('it sets pill-value active after selecting an operator', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(metaPowerSelect, powerSelectOption, 0);// option A
    await waitUntil(() => find(operatorPowerSelect));
    selectChoose(operatorPowerSelect, powerSelectOption, 0);// option =
    await waitUntil(() => find(operator));
    assert.equal(findAll(valueInput).length, 1, 'Missing value input field');
  });

  test('it allows you to edit the meta after it was selected', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    // Select meta option A
    await selectChoose(meta, powerSelectOption, 0);
    assert.equal(trim(find(meta).textContent), 'a');
    // Verify that operator gets control
    await focus(operatorPowerSelect);
    assert.equal(findAll(powerSelectOption).length, 7);
    // Click back on meta and verify that 1 down-selected option is visible
    await click(meta);
    await focus(metaPowerSelect);
    assert.equal(findAll(powerSelectOption).length, 1);
    // Clear input to show all meta options
    await fillIn(metaInput, '');
    assert.equal(findAll(powerSelectOption).length, 3);
    // Select meta options B
    await selectChoose(meta, powerSelectOption, 1);
    assert.equal(trim(find(meta).textContent), 'b');
  });
});