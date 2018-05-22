import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { fillIn, find, findAll, render, settled, triggerKeyEvent, waitUntil } from '@ember/test-helpers';
import sinon from 'sinon';

import { patchReducer } from '../../../../helpers/vnext-patch';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';

const ENTER_KEY = '13';
const X_KEY = '88';

const metaPowerSelect = '.pill-meta .ember-power-select-trigger';
const operatorPowerSelect = '.pill-operator .ember-power-select-trigger';
const powerSelectOption = '.ember-power-select-option';
const value = '.pill-value input';

const initialState = {
  dictionaries: {
    language: [
      { count: 0, format: 'Text', metaName: 'a', flags: 1, displayName: 'A' },
      { count: 0, format: 'Text', metaName: 'b', flags: 2, displayName: 'B' },
      { count: 0, format: 'Text', metaName: 'c', flags: 3, displayName: 'C' }
    ]
  },
  nextGen: {
    pillsData: []
  }
};

let setState;
const newActionSpy = sinon.spy(nextGenCreators, 'addNextGenPill');

module('Integration | Component | Query Pills', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { investigate: state };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  hooks.afterEach(function() {
    newActionSpy.reset();
  });

  hooks.after(function() {
    newActionSpy.restore();
  });

  test('Upon initialization, one active pill is created', async function(assert) {
    await render(hbs`{{query-container/query-pills}}`);
    assert.equal(findAll('.query-pill').length, 1, 'There should only be one query-pill.');
  });

  test('Creating a pill sets filters and sends action for redux state update', async function(assert) {
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
    await triggerKeyEvent(value, 'keydown', X_KEY);// x
    await triggerKeyEvent(value, 'keydown', ENTER_KEY);

    return settled().then(async () => {
      // Internal (temporary) filters maintained
      const filters = this.get('filters');
      assert.equal(filters.length, 1, 'A filter was not created');

      // action to store in state called
      assert.equal(newActionSpy.callCount, 1, 'The add pill action creator was called once');
      assert.deepEqual(
        newActionSpy.args[0][0],
        { pillData: { meta: 'a', operator: '=', value: 'x' }, position: 0 },
        'The action creator was called with the right arguments'
      );
    });
  });

  test('newPillPosition is set correctly', async function(assert) {
    setState({
      ...initialState,
      nextGen: {
        pillsData: [1, 2, 3]
      }
    });

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
    await triggerKeyEvent(value, 'keydown', X_KEY);// x
    await triggerKeyEvent(value, 'keydown', ENTER_KEY);

    return settled().then(async () => {
      // action to store in state called
      assert.deepEqual(
        newActionSpy.args[0][0].position,
        3,
        'the position is correct'
      );
    });
  });
});