import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render, settled, triggerKeyEvent } from '@ember/test-helpers';
import sinon from 'sinon';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';
import { createBasicPill } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const ESCAPE_KEY = KEY_MAP.escape.code;

let setState;
const newActionSpy = sinon.spy(nextGenCreators, 'addNextGenPill');
const deleteActionSpy = sinon.spy(nextGenCreators, 'deleteNextGenPill');

const allPillsAreClosed = (assert) => {
  assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 0, 'Class for pill open should not be present.');
  assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'Class for pills open for edit.');
  assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 0, 'Class for trigger open should not be present.');
};

module('Integration | Component | query-pills', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  hooks.afterEach(function() {
    newActionSpy.reset();
    deleteActionSpy.reset();
  });

  hooks.after(function() {
    newActionSpy.restore();
    deleteActionSpy.restore();
  });

  test('Upon initialization, one active pill is created', async function(assert) {
    await render(hbs`{{query-container/query-pills}}`);
    assert.equal(findAll(PILL_SELECTORS.allPills).length, 1, 'There should only be one query-pill.');
  });

  test('Creating a pill sets filters and sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    await createBasicPill();

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
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    await createBasicPill();

    return settled().then(async () => {
      // action to store in state called
      assert.deepEqual(
        newActionSpy.args[0][0].position,
        2,
        'the position is correct'
      );
    });
  });

  test('new pill triggers render appropriately', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 1, 'There should only be one new pill trigger.');

    await createBasicPill();
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 2, 'There should now be two new pill triggers.');
  });

  test('Creating a pill with the new pill trigger sets filters and sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    await createBasicPill(true);

    // Internal (temporary) filters maintained
    const filters = this.get('filters');
    assert.equal(filters.length, 3, 'A filter was not created');

    // action to store in state called
    assert.equal(newActionSpy.callCount, 1, 'The add pill action creator was called once');
    assert.deepEqual(
      newActionSpy.args[0][0],
      { pillData: { meta: 'a', operator: '=', value: 'x' }, position: 1 },
      'The action creator was called with the right arguments including the proper position'
    );
  });

  test('Deleting a pill sets filters and sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    await click(PILL_SELECTORS.deletePill);

    return settled().then(async () => {
      // Internal (temporary) filters maintained
      const filters = this.get('filters');
      assert.equal(filters.length, 1, 'Down to one filter');

      // action to store in state called
      assert.equal(deleteActionSpy.callCount, 1, 'The delete pill action creator was called once');
      assert.deepEqual(
        deleteActionSpy.args[0][0],
        { pillData: { id: '1', meta: 'a', operator: '=', value: 'x' } },
        'The action creator was called with the right arguments'
      );
    });
  });

  test('Creating a pill leaves no classes indicating pills are open', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);
    await createBasicPill(true);

    allPillsAreClosed(assert);
  });

  test('Cancelling out of pill creation leaves no classes indicating pills are open', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);

    allPillsAreClosed(assert);
  });

  test('Beginning creation of a pill template adds specific classes to container', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);

    assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 1, 'Class for pill open should be present.');
    assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'No classes for pills open for edit');
    assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 0, 'Class for trigger open should be present.');
  });

  test('Beginning creation of a pill from trigger adds appropriate classes', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);

    // escape out of template first
    await focus(PILL_SELECTORS.metaPowerSelect);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);

    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);

    assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 1, 'Class for pill open should be present.');
    assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'No classes for pills open for edit');
    assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 1, 'Class for trigger open should be present.');
  });

});