import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, triggerEvent, render, settled, triggerKeyEvent } from '@ember/test-helpers';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import guidedCreators from 'investigate-events/actions/guided-creators';
import { createBasicPill, doubleClick, elementIsVisible, leaveNewPillTemplate } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';
import { throwSocket } from '../../../../helpers/patch-socket';
import { invalidServerResponseText } from '../../../../unit/actions/data';

const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const DELETE_KEY = KEY_MAP.delete.code;
const BACKSPACE_KEY = KEY_MAP.backspace.code;

let setState;
const newActionSpy = sinon.spy(guidedCreators, 'addGuidedPill');
const deleteActionSpy = sinon.spy(guidedCreators, 'deleteGuidedPill');
const selectActionSpy = sinon.spy(guidedCreators, 'selectGuidedPills');
const deselectActionSpy = sinon.spy(guidedCreators, 'deselectGuidedPills');
const openGuidedPillForEditSpy = sinon.spy(guidedCreators, 'openGuidedPillForEdit');
const resetGuidedPillSpy = sinon.spy(guidedCreators, 'resetGuidedPill');

const allPillsAreClosed = (assert) => {
  assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 0, 'Class for pill open should not be present.');
  assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'Class for pills open for edit.');
  assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 0, 'Class for trigger open should not be present.');
};

const e = {
  clientX: 100,
  clientY: 100,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};

const wormhole = 'wormhole-context-menu';

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
    openGuidedPillForEditSpy.reset();
    resetGuidedPillSpy.reset();
  });

  hooks.after(function() {
    newActionSpy.restore();
    deleteActionSpy.restore();
    selectActionSpy.restore();
    deselectActionSpy.restore();
    openGuidedPillForEditSpy.restore();
    resetGuidedPillSpy.restore();
  });

  test('Upon initialization, one active pill is created', async function(assert) {
    await render(hbs`{{query-container/query-pills}}`);
    assert.equal(findAll(PILL_SELECTORS.allPills).length, 1, 'There should only be one query-pill.');
  });

  test('Creating a pill sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(false)
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await createBasicPill();

    return settled().then(async () => {
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
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
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
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 2, 'There should two new pill triggers.');

    await createBasicPill();
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 3, 'There should now be three new pill triggers.');
  });

  test('Creating a pill with the new pill trigger sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    await createBasicPill(true);

    // action to store in state called
    assert.equal(newActionSpy.callCount, 1, 'The add pill action creator was called once');
    assert.deepEqual(
      newActionSpy.args[0][0],
      { pillData: { meta: 'a', operator: '=', value: '\'x\'' }, position: 0 },
      'The action creator was called with the right arguments including the proper position'
    );
  });

  test('Deleting a pill sends action for redux state update', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true }}`);
    await leaveNewPillTemplate();
    await click(PILL_SELECTORS.deletePill);

    return settled().then(async () => {
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
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await click(PILL_SELECTORS.deletePill);

    return settled().then(async () => {
      // action to store in state called
      assert.equal(deleteActionSpy.callCount, 0, 'The delete pill action creator was called once');
    });
  });

  test('Attempting to edit a pill while a new pill is open will not open the pill for edit', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async () => {
      assert.equal(openGuidedPillForEditSpy.callCount, 0, 'The openGuidedPillForEditSpy pill action not called at all');
    });
  });

  test('Creating a pill leaves no classes indicating pills are open', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);
    await createBasicPill(true);

    allPillsAreClosed(assert);
  });

  test('Cancelling out of pill creation leaves no classes indicating pills are open', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await leaveNewPillTemplate();
    allPillsAreClosed(assert);
  });

  test('Beginning creation of a pill template adds specific classes to container', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 1, 'Class for pill open should be present.');
    assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'No classes for pills open for edit');
    assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 0, 'Class for trigger open should be present.');
  });

  test('Beginning creation of a pill from trigger adds appropriate classes', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    // escape out of template first
    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);

    assert.equal(findAll(PILL_SELECTORS.pillOpen).length, 1, 'Class for pill open should be present.');
    assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 0, 'No classes for pills open for edit');
    assert.equal(findAll(PILL_SELECTORS.pillTriggerOpenForAdd).length, 1, 'Class for trigger open should be present.');
  });

  test('Creating a pill validates the pill(clientSide) and updates if necessary', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    // creates a pill with TimeT format with a text value 'x'
    // will create an invalid pill once redux updates the store
    await createBasicPill(false, 'TimeT');
    // component class updates when store is updated
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1, 'Class for invalid pill should be present');
    assert.equal(this.$(PILL_SELECTORS.invalidPill).prop('title'), 'You must enter a valid date.', 'Expected title with the error message');
  });

  test('Creating a pill validates the pill (serverSide) and updates if necessary', async function(assert) {
    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'core-query-validate', message: invalidServerResponseText });
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    await createBasicPill(false, 'Text');
    // component class updates when store is updated
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1, 'Class for invalid pill should be present');
    assert.equal(this.$(PILL_SELECTORS.invalidPill).prop('title'), 'Invalid server response', 'Expected title with the error message');
    done();
  });

  test('Clicking an inactive pill sends it to state to be selected', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
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
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
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
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
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
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
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
      .canQueryGuided()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    assert.equal(findAll(PILL_SELECTORS.activePills).length, 2, 'Two active pills, one is the end of line template.');
  });

  test('clicking escape inside an editing pill will message out', async function(assert) {
    const { pillsData } = new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build()
      .investigate
      .queryNode;

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    await focus(PILL_SELECTORS.triggerMetaPowerSelect);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);

    assert.equal(resetGuidedPillSpy.callCount, 1, 'The reset pill action creator was called once');
    const [ [ calledWith ] ] = resetGuidedPillSpy.args;
    assert.deepEqual(calledWith.id, pillsData[0].id, 'shows as being selected as is being sent to be deselected');
  });


  test('If a pill is doubled clicked, a message will be sent to  mark it for editing', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    await leaveNewPillTemplate();

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async () => {
      // action to store in state called
      assert.equal(openGuidedPillForEditSpy.callCount, 1, 'The openGuidedPillForEditSpy pill action creator was called once');
    });
  });

  test('Opening a pill for edit will deselect other pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await click(`#${metas[1].id}`); // make it selected

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Two selecteded pills.');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be two pills plus template.');

    await leaveNewPillTemplate();
    doubleClick(PILL_SELECTORS.queryPill);

    return settled().then(async () => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Pills no longer selected');
    });
  });

  test('Attempting to delete a pill while a pill is being edited will not work', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    doubleClick(PILL_SELECTORS.queryPill);
    await click(PILL_SELECTORS.deletePill);
    assert.equal(deleteActionSpy.callCount, 0, 'The delete pill action creator wasn\'t called');
  });

  test('While a pill is being edited you cannot edit another pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    assert.equal(findAll(PILL_SELECTORS.activePills).length, 1, 'One active pill, at the end of line template.');

    await leaveNewPillTemplate();

    const pills = findAll(PILL_SELECTORS.meta);
    doubleClick(`#${pills[0].id}`, true); // open pill for edit
    assert.equal(openGuidedPillForEditSpy.callCount, 1, 'The openGuidedPillForEditSpy pill action creator was called once');
    assert.equal(findAll(PILL_SELECTORS.activePills).length, 2, 'Now two active pills');

    doubleClick(`#${pills[1].id}`); // attempt to open another pill for edit
    assert.equal(openGuidedPillForEditSpy.callCount, 1, 'The openGuidedPillForEditSpy pill action still just called once');
    assert.equal(findAll(PILL_SELECTORS.activePills).length, 2, 'Still two active pills');
  });

  test('While a pill is being edited you cannot click to add a pill using trigger or template', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.activePills).length, 1, 'One active pill, at the end of line template.');
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

  test('An expensive pill displays as expensive', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await createBasicPill(false, 'Text', 'contains');
    assert.equal(findAll(PILL_SELECTORS.expensivePill).length, 1, 'Class for expensive pill should be present');
  });

  test('complex pills will be rendered', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 1, 'A complex pill should be present');
  });

  test('Right clicking on a selected pill should trigger contextMenu event AND not trigger the same when not selected', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(4);
    const done = assert.async();
    let count = 0;
    document.addEventListener('contextmenu', () => {
      assert.ok('called when right clicked on a selected pill');
      count++;
    });

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected

    triggerEvent(PILL_SELECTORS.selectedPill, 'contextmenu', e);

    return settled().then(() => {
      // right click on a un-selected pill( the 2nd one), should not trigger contextMenu event
      triggerEvent(PILL_SELECTORS.expensivePill, 'contextmenu', e);
      assert.equal(this.$('.content-context-menu').length, 1, 'one menu');
      assert.equal(count, 1, 'Should be called once');
      done();
    });
  });

  // --- Not Working ---
  // Can see that it goes inside ember-context-menu/mixins -
  // triggers activate - https://github.com/cbroeren/ember-context-menu/blob/40f1ccb8cf1dbb77589721b1f506e05b323adc54/addon/mixins/context-menu.js#L20

  // After which the event can be tracked down inside the service where
  // it sets the isActive flag , ember-context-menu/service - https://github.com/cbroeren/ember-context-menu/blob/40f1ccb8cf1dbb77589721b1f506e05b323adc54/addon/services/context-menu.js#L46

  // But the component, ember-context-menu/components/context-menu is never rendered.
  // Can see that the template, ember-context-menu/templates/context-menu needs isActive,
  // which it should get. But it is never called.
  skip('Right clicking on a selected pill will open a context menu with options  ---- not Working', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    const done = assert.async();
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected


    triggerEvent(PILL_SELECTORS.selectedPill, 'contextmenu', e);

    // triggerEvent(PILL_SELECTORS.selectedPill, 'contextmenu', e);

    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 3);
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Number of pills present');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'One selecteded pill.');
      done();
    });
  });

  test('Pressing Delete key once a pill is selected will delete it', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(5);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Should be one pill plus template.');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no pill selected.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing Backspace key once a pill is selected will delete it', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(5);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Should be one pill plus template.');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no pill selected.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing Delete key once a complex pill is selected will delete it', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    assert.expect(5);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    await click(PILL_SELECTORS.complexPill); // make the complex pill selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'Should be no complex pill');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no pill selected.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing backspace key once a complex pill is selected will delete it', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    assert.expect(5);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    await click(PILL_SELECTORS.complexPill); // make the complex pill selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'Should be no complex pill');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no pill selected.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing ENTER when there are no pills will submit a query', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    assert.expect(0);
    const done = assert.async(1);

    this.set('executeQuery', () => {
      done();
    });

    await render(hbs`
      {{query-container/query-pills executeQuery=executeQuery}}
    `);

    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ENTER_KEY);
  });

  test('Pressing ENTER when there are pills will submit a query', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(0);
    const done = assert.async(1);

    this.set('executeQuery', () => {
      done();
    });

    await render(hbs`
      {{query-container/query-pills executeQuery=executeQuery}}
    `);

    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ENTER_KEY);
  });

  test('Pressing ENTER when there are invalid pills will NOT submit a query', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .invalidPillsDataPopulated()
      .build();

    assert.expect(0);

    this.set('executeQuery', () => {
      assert.ok(false);
    });

    await render(hbs`
      {{query-container/query-pills executeQuery=executeQuery}}
    `);

    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ENTER_KEY);
  });

  test('Pressing ENTER key once a pill is selected will open it for edit', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(5);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
      assert.equal(findAll(PILL_SELECTORS.activeQueryPill).length, 2, '1 active pill and 1 new pill trigger(active)should be present');
      assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 1, 'should have 1 pill open for editing');
    });
  });

  test('Pressing ENTER key once a complex pill is selected will open it for edit', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    assert.expect(6);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    await click(PILL_SELECTORS.complexPill); // make the complex pill selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no pill selected.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
      assert.equal(findAll(PILL_SELECTORS.complexPillActive).length, 1, 'active complex pill should be present');
      assert.equal(findAll(PILL_SELECTORS.complexPillInput).length, 1, 'complex pill input should be present');
    });
  });
});