import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, render, triggerKeyEvent } from '@ember/test-helpers';
import sinon from 'sinon';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';

const addFreeFormFilterSpy = sinon.spy(nextGenCreators, 'addFreeFormFilter');

const ENTER_KEY = KEY_MAP.enter.code;

let setState;

module('Integration | Component | free-form', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  hooks.afterEach(function() {
    addFreeFormFilterSpy.reset();
  });

  hooks.after(function() {
    addFreeFormFilterSpy.restore();
  });

  test('it triggers execute query action when user enters text and presses enter', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).pillsDataEmpty().hasRequiredValuesToQuery(true).build();

    this.set('executeQuery', () => {
      assert.ok(true, 'Should execute query');
    });

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, 'medium = 1');
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);

    assert.equal(addFreeFormFilterSpy.callCount, 1, 'The add pill action creator was called once');
    assert.deepEqual(
      addFreeFormFilterSpy.args[0][0],
      'medium = 1',
      'The action creator was called with the right arguments'
    );

  });

  test('it does not trigger an action to add a filter if the text hasnt changed', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState).pillsDataPopulated().hasRequiredValuesToQuery(true).build();

    this.set('executeQuery', () => {});

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);
    await click(PILL_SELECTORS.freeFormInput);
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);

    assert.equal(addFreeFormFilterSpy.callCount, 0, 'The add pill action creator was not called');
  });

  test('it does not trigger search action when hasRequiredValuesToQuery isn\'t set', async function(assert) {
    assert.expect(0);

    new ReduxDataHelper(setState)
      .pillsDataPopulated()
      .hasRequiredValuesToQuery(false)
      .build();

    this.set('executeQuery', () => {
      assert.ok(false, 'Should not get here');
    });

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);
  });

  test('it does not add any filters to state if the text is empty', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {
      assert.ok(true, 'Should get here');
    });

    await render(hbs`{{query-container/free-form executeQuery=(action executeQuery)}}`);

    await click(PILL_SELECTORS.freeFormInput);
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);

    assert.equal(addFreeFormFilterSpy.callCount, 0, 'The add pill action creator was not called');
  });

});