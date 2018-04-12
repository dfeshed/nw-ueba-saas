import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { find, findAll, render, waitUntil } from '@ember/test-helpers';

const meta = '.pill-meta';
const metaPowerSelect = '.pill-meta .ember-power-select-trigger';
const operator = '.pill-operator';
const operatorPowerSelect = '.pill-operator .ember-power-select-trigger';
const powerSelectOption = '.ember-power-select-option';
const value = '.pill-value input';
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

  test('it activates meta-pill if active upon initialization', async function(assert) {
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

  test('it sets meta-value active after selecting an operator', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(metaPowerSelect, powerSelectOption, 0);// option A
    await waitUntil(() => find(operatorPowerSelect));
    selectChoose(operatorPowerSelect, powerSelectOption, 0);// option =
    await waitUntil(() => find(operator));
    assert.equal(findAll(value).length, 1, 'Missing value input field');
  });
});