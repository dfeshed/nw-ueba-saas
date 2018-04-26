import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { fillIn, find, findAll, render, settled, triggerKeyEvent, waitUntil } from '@ember/test-helpers';

const ENTER_KEY = '13';

const metaPowerSelect = '.pill-meta .ember-power-select-trigger';
const operatorPowerSelect = '.pill-operator .ember-power-select-trigger';
const powerSelectOption = '.ember-power-select-option';
const value = '.pill-value input';

const initialState = {
  language: [
    { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' },
    { count: 0, format: 'Text', metaName: 'b', flags: 1, displayName: 'B' },
    { count: 0, format: 'Text', metaName: 'c', flags: 1, displayName: 'C' }
  ]
};

let setState;

module('Integration | Component | Query Pills', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { investigate: { dictionaries: state } };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  test('Upon initialization, one active pill is created and tracked', async function(assert) {
    await render(hbs`{{query-container/query-pills}}`);
    assert.equal(findAll('.query-pill').length, 1, 'There should only be one query-pill.');
  });

  test('It creates a pill when supplied with meta, operator, and value', async function(assert) {
    setState({ ...initialState });
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    // Choose the first meta option
    selectChoose(metaPowerSelect, powerSelectOption, 0);// option A
    await waitUntil(() => find(operatorPowerSelect));
    // Choose the first operator option
    selectChoose(operatorPowerSelect, powerSelectOption, 0);// option =
    await waitUntil(() => find(value));
    // Fill in the value, to properly simulate the event we need to fillIn AND
    // triggerKeyEvent for the "x" character.
    await fillIn(value, 'x');
    await triggerKeyEvent(value, 'keyup', '88');// x
    await triggerKeyEvent(value, 'keyup', ENTER_KEY);

    return settled().then(async () => {
      const filters = this.get('filters');
      assert.equal(filters.length, 1, 'A filter was not created');
    });
  });

  test('It creates a pill when supplied with meta and operator that does not accept a value', async function(assert) {
    setState({ ...initialState });
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    // Choose the first meta option
    selectChoose(metaPowerSelect, powerSelectOption, 0);// option A
    await waitUntil(() => find(operatorPowerSelect));
    // Choose the first operator option
    selectChoose(operatorPowerSelect, powerSelectOption, 2);// option exists

    return settled().then(async () => {
      const filters = this.get('filters');
      assert.equal(filters.length, 1, 'A filter was not created');
    });
  });
});