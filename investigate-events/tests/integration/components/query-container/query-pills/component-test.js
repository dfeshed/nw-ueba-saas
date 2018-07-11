import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render, settled, triggerKeyEvent } from '@ember/test-helpers';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';
import { createBasicPill, doubleClick, elementIsVisible, leaveNewPillTemplate } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';
import { throwSocket } from '../../../../helpers/patch-socket';
import { invalidServerResponseText } from '../../../../unit/actions/data';

const ESCAPE_KEY = KEY_MAP.escape.code;

let setState;
const newActionSpy = sinon.spy(nextGenCreators, 'addNextGenPill');
const deleteActionSpy = sinon.spy(nextGenCreators, 'deleteNextGenPill');
const selectActionSpy = sinon.spy(nextGenCreators, 'selectNextGenPills');
const deselectActionSpy = sinon.spy(nextGenCreators, 'deselectNextGenPills');
const openNextGenPillForEditSpy = sinon.spy(nextGenCreators, 'openNextGenPillForEdit');

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
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    newActionSpy.reset();
    deleteActionSpy.reset();
    selectActionSpy.reset();
    deselectActionSpy.reset();
    openNextGenPillForEditSpy.reset();
  });

  hooks.after(function() {
    newActionSpy.restore();
    deleteActionSpy.restore();
    selectActionSpy.restore();
    deselectActionSpy.restore();
    openNextGenPillForEditSpy.restore();
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
        { pillData: { meta: 'a', operator: '=', value: '\'x\'' }, position: 0 },
        'The action creator was called with the right arguments'
      );
      assert.equal(this.$(PILL_SELECTORS.queryPill).prop('title'), 'a = \'x\'', 'Expected stringified pill');
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
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 2, 'There should two new pill triggers.');

    await createBasicPill();
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 3, 'There should now be three new pill triggers.');
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
      { pillData: { meta: 'a', operator: '=', value: '\'x\'' }, position: 0 },
      'The action creator was called with the right arguments including the proper position'
    );
  });

  test('Deleting a pill sets filters and sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    await leaveNewPillTemplate();
    await click(PILL_SELECTORS.deletePill);

    return settled().then(async () => {
      // Internal (temporary) filters maintained
      const filters = this.get('filters');
      assert.equal(filters.length, 1, 'Down to one filter');

      // action to store in state called
      assert.equal(deleteActionSpy.callCount, 1, 'The delete pill action creator was called once');
      assert.deepEqual(
        deleteActionSpy.args[0][0],
        { pillData: { id: '1', meta: 'a', operator: '=', value: '\'x\'', isSelected: false } },
        'The action creator was called with the right arguments'
      );
    });
  });

  test('Attempting to delete a pill while a new pill is open will not delete the pill', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    await click(PILL_SELECTORS.deletePill);

    return settled().then(async () => {
      // Internal (temporary) filters maintained
      const filters = this.get('filters');
      // Filters never gets set inside the component, so it remains the empty
      // array that it is when passed in
      assert.equal(filters.length, 0, 'No filters');

      // action to store in state called
      assert.equal(deleteActionSpy.callCount, 0, 'The delete pill action creator was called once');
    });
  });

  test('Attempting to edit a pill while a new pill is open will not open the pill for edit', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async () => {
      assert.equal(openNextGenPillForEditSpy.callCount, 0, 'The openNextGenPillForEditSpy pill action not called at all');
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
    await leaveNewPillTemplate();
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
    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);

    assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 1, 'Class for pill open should be present.');
    assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'No classes for pills open for edit');
    assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 1, 'Class for trigger open should be present.');
  });

  test('Creating a pill sets filters, validates the pill(clientSide) and updates if necessary', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);

    // creates a pill with TimeT format with a text value 'x'
    // will create an invalid pill once redux updates the store
    await createBasicPill(false, 'TimeT');
    // component class updates when store is updated
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1, 'Class for invalid pill should be present');
    assert.equal(this.$(PILL_SELECTORS.invalidPill).prop('title'), 'You must enter a valid date.', 'Expected title with the error message');
  });

  test('Creating a pill sets filters, validates the pill (serverSide) and updates if necessary', async function(assert) {
    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'core-query-validate', message: invalidServerResponseText });
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);

    await createBasicPill(false, 'Text');
    // component class updates when store is updated
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1, 'Class for invalid pill should be present');
    assert.equal(this.$(PILL_SELECTORS.invalidPill).prop('title'), 'Invalid server response', 'Expected title with the error message');
    done();
  });

  test('Clicking an inactive pill sends it to state to be selected', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    await click(PILL_SELECTORS.meta);

    return settled().then(async () => {
      // action to store in state called
      assert.equal(selectActionSpy.callCount, 1, 'The select pill action creator was called once');
      assert.deepEqual(
        selectActionSpy.args[0][0],
        { pillData: [ { id: '1', meta: 'a', operator: '=', value: '\'x\'', isSelected: false } ] },
        'The action creator was called with the right arguments'
      );
    });
  });

  test('Clicking an inactive pill that is selected sends it to state to be deselected', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();
    this.set('filters', []);

    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    await click(PILL_SELECTORS.meta); // make it selected
    await click(PILL_SELECTORS.meta); // make it deselected

    return settled().then(async () => {
      // action to store in state called
      assert.equal(deselectActionSpy.callCount, 1, 'The deselect pill action creator was called once');
      const [ [ calledWith ] ] = deselectActionSpy.args;
      assert.equal(calledWith.pillData[0].isSelected, true, 'shows as being selected as is being sent to be deselected');
    });
  });

  test('Deleting a pill removes selected class from other pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await click(`#${metas[1].id}`); // make it selected

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Two selecteded pills.');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be two pills plus template.');

    await focus(PILL_SELECTORS.triggerMetaPowerSelect);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
    await click(PILL_SELECTORS.deletePill);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Pill no longer selected');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Should be one pill plus template.');
  });

  test('Clicking new pill trigger will deselect other pills and open new pill trigger', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await click(`#${metas[1].id}`); // make it selected

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Two selecteded pills.');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be two pills plus template.');

    await click(PILL_SELECTORS.newPillTrigger);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Pill no longer selected');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 4, 'Should be two pills plus template plus triggered pill.');
  });

  test('If a pill is being edited, it is active', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);

    assert.equal(findAll(PILL_SELECTORS.activePill).length, 2, 'Two active pills, one is the end of line template.');
  });

  test('If a pill is doubled clicked, a message will be sent to  mark it for editing', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);

    await leaveNewPillTemplate();

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async () => {
      // action to store in state called
      assert.equal(openNextGenPillForEditSpy.callCount, 1, 'The openNextGenPillForEditSpy pill action creator was called once');
    });
  });

  test('Opening a pill for edit will deselect other pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataPopulated()
      .build();

    this.set('filters', []);

    await render(hbs`{{query-container/query-pills filters=filters isActive=true}}`);
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await click(`#${metas[1].id}`); // make it selected

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Two selecteded pills.');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be two pills plus template.');

    doubleClick(PILL_SELECTORS.queryPill);

    return settled().then(async () => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Pills no longer selected');
    });
  });

  test('Attempting to delete a pill while a pill is being edited will not work', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);
    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);
    doubleClick(PILL_SELECTORS.queryPill);
    await click(PILL_SELECTORS.deletePill);
    assert.equal(deleteActionSpy.callCount, 0, 'The delete pill action creator wasn\'t called');
  });

  test('While a pill is being edited you cannot edit another pill', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);
    await render(hbs`{{query-container/query-pills isActive=true filters=filters}}`);

    assert.equal(findAll(PILL_SELECTORS.activePill).length, 1, 'One active pill, at the end of line template.');

    await leaveNewPillTemplate();

    const pills = findAll(PILL_SELECTORS.meta);
    doubleClick(`#${pills[0].id}`, true); // open pill for edit
    assert.equal(openNextGenPillForEditSpy.callCount, 1, 'The openNextGenPillForEditSpy pill action creator was called once');
    assert.equal(findAll(PILL_SELECTORS.activePill).length, 2, 'Now two active pills');

    doubleClick(`#${pills[1].id}`); // attempt to open another pill for edit
    assert.equal(openNextGenPillForEditSpy.callCount, 1, 'The openNextGenPillForEditSpy pill action still just called once');
    assert.equal(findAll(PILL_SELECTORS.activePill).length, 2, 'Still two active pills');
  });

  test('While a pill is being edited you cannot click to add a pill using trigger or template', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataPopulated().build();
    this.set('filters', []);
    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true filters=filters}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.activePill).length, 1, 'One active pill, at the end of line template.');
    await leaveNewPillTemplate();
    const pills = findAll(PILL_SELECTORS.queryPill);
    doubleClick(`#${pills[0].id}`); // open pill for edit

    return settled().then(async () => {
      const triggers = findAll(PILL_SELECTORS.newPillTrigger);
      assert.equal(triggers.length, 2, 'Two triggers...');
      assert.equal(elementIsVisible(triggers[0]), false, '...but first is not visible...');
      assert.equal(elementIsVisible(triggers[1]), false, '...and neither is 2nd...');

      const template = findAll(PILL_SELECTORS.newPillTemplate);
      assert.equal(template.length, 1, 'One template...');
      assert.equal(elementIsVisible(template[0]), false, '...but not visible');
    });
  });

});