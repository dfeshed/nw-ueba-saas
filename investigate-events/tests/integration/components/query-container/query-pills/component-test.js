import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, findAll, find, triggerEvent, render, settled, triggerKeyEvent, waitUntil, typeIn } from '@ember/test-helpers';
import { clickTrigger, typeInSearch, selectChoose } from 'ember-power-select/test-support/helpers';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import guidedCreators from 'investigate-events/actions/guided-creators';
import { createBasicPill, doubleClick, elementIsVisible, leaveNewPillTemplate, toggleTab } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';
import { throwSocket } from '../../../../helpers/patch-socket';
import { invalidServerResponseText } from '../../../../unit/actions/data';
import {
  AFTER_OPTION_TEXT_LABEL
} from 'investigate-events/constants/pill';
import initializationCreators from 'investigate-events/actions/initialization-creators';

const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.key;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.key;
const ARROW_DOWN_KEY = KEY_MAP.arrowDown.key;
const BACKSPACE_KEY = KEY_MAP.backspace.key;
const CLOSE_PAREN_KEY = KEY_MAP.closeParen.key;
const DELETE_KEY = KEY_MAP.delete.key;
const ENTER_KEY = KEY_MAP.enter.key;
const ESCAPE_KEY = KEY_MAP.escape.key;
const SPACE_KEY = KEY_MAP.space.key;
const modifiers = { shiftKey: true };

const newActionSpy = sinon.spy(guidedCreators, 'addGuidedPill');
const deleteActionSpy = sinon.spy(guidedCreators, 'deleteGuidedPill');
const editGuidedPillSpy = sinon.spy(guidedCreators, 'editGuidedPill');
const selectActionSpy = sinon.spy(guidedCreators, 'selectGuidedPills');
const deselectActionSpy = sinon.spy(guidedCreators, 'deselectGuidedPills');
const openGuidedPillForEditSpy = sinon.spy(guidedCreators, 'openGuidedPillForEdit');
const resetGuidedPillSpy = sinon.spy(guidedCreators, 'resetGuidedPill');
const selectAllPillsTowardsDirectionSpy = sinon.spy(guidedCreators, 'selectAllPillsTowardsDirection');
const deleteSelectedGuidedPillsSpy = sinon.spy(guidedCreators, 'deleteSelectedGuidedPills');
const recentQueriesSpy = sinon.spy(initializationCreators, 'getRecentQueries');
const batchAddQueriesSpy = sinon.spy(guidedCreators, 'batchAddPills');
const valueSuggestionsSpy = sinon.spy(initializationCreators, 'valueSuggestions');
// const addFreeFormFilterSpy = sinon.spy(guidedCreators, 'addFreeFormFilterSpy');
const spys = [
  newActionSpy, deleteActionSpy, editGuidedPillSpy, selectActionSpy,
  deselectActionSpy, openGuidedPillForEditSpy, resetGuidedPillSpy,
  selectAllPillsTowardsDirectionSpy, deleteSelectedGuidedPillsSpy,
  recentQueriesSpy, batchAddQueriesSpy, valueSuggestionsSpy
];

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

const trim = (text) => text.replace(/\s+/g, '').trim();

const wormhole = 'wormhole-context-menu';

let setState, contextEventListenerCallback;

module('Integration | Component | Query Pills', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
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
    assert.ok(newActionSpy.calledOnce, 'The addGuidedPill creator was not called once');
    assert.propEqual(newActionSpy.args[0][0],
      { pillData: { meta: 'a', operator: '=', value: '\'x\'', type: 'query' }, position: 0, shouldAddFocusToNewPill: false },
      'The addGuidedPill creator was returned the wrong arguments');
  });

  test('newPillPosition is set correctly', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await createBasicPill();

    return settled().then(async() => {
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

  test('Creating a pill in the middle of pills creates a focused pill', async function(assert) {
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
    assert.propEqual(
      newActionSpy.args[0][0],
      { pillData: { meta: 'a', operator: '=', value: '\'x\'', type: 'query' }, position: 0, shouldAddFocusToNewPill: true },
      'The action creator was called with the right arguments including the proper position'
    );
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
  });

  test('Creating a focused pill and clicking outside the query-pills component should remove focus', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{#rsa-application-content}}
          <div class='outside'>
            {{query-container/query-pills isActive=true}}
          </div>
        {{/rsa-application-content}}
      </div>
    `);
    await leaveNewPillTemplate();
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be 1 pill focused');

    await click('.outside');

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Should be no pill focused');
  });

  test('Creating a focused pill and clicking on the query-pill should not remove focus from it', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{#rsa-application-content}}
          <div class='outside'>
            {{query-container/query-pills isActive=true}}
          </div>
        {{/rsa-application-content}}
      </div>
    `);
    await leaveNewPillTemplate();
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be 1 pill focused');

    await click(PILL_SELECTORS.queryPill);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be 1 pill focused');
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

    return settled().then(async() => {
      // action to store in state called
      assert.equal(deleteActionSpy.callCount, 1, 'The delete pill action creator was called once');
      assert.propEqual(
        deleteActionSpy.args[0][0],
        { pillData: [{ id: '1', meta: 'a', operator: '=', value: '\'x\'', type: 'query', isSelected: false, isFocused: false }] },
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

    return settled().then(async() => {
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

    return settled().then(async() => {
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

    // creates a pill with MAC format with a text value 'x'
    // will create an invalid pill once redux updates the store
    await createBasicPill(false, 'MAC');
    // component class updates when store is updated
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1, 'Class for invalid pill should be present');
    assert.equal(find(PILL_SELECTORS.invalidPill).getAttribute('title'), 'You entered \'x\'. You must enter a MAC address.', 'Expected title with the error message');
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
    assert.equal(find(PILL_SELECTORS.invalidPill).getAttribute('title'), 'You entered \'\'x\'\'. Invalid server response', 'Expected title with the error message');
    done();
  });

  test('Clicking an inactive pill sends it to state to be selected', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.meta);

    return settled().then(async() => {
      // action to store in state called
      assert.equal(selectActionSpy.callCount, 1, 'The select pill action creator was called once');
      assert.propEqual(
        selectActionSpy.args[0][0],
        { pillData: [ { id: '1', meta: 'a', operator: '=', value: '\'x\'', type: 'query', isSelected: false, isFocused: false } ] },
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

    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.meta); // make it selected
    await click(PILL_SELECTORS.meta); // make it deselected

    return settled().then(async() => {
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

    await leaveNewPillTemplate();

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

    await leaveNewPillTemplate();

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

  // Ember 3.3
  skip('clicking escape inside an editing pill will message out', async function(assert) {
    const { pillsData } = new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build()
      .investigate
      .queryNode;

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    await click(PILL_SELECTORS.meta);
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

    return settled().then(async() => {
      // action to store in state called
      assert.equal(openGuidedPillForEditSpy.callCount, 1, 'The openGuidedPillForEditSpy pill action creator was called once');
    });
  });

  // Ember 3.3 Can't figure why this would suddenly start failing
  skip('If a focused pill is doubled clicked and opened for edit, it will no longer be focused, but will regain focus once escaped', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .markFocused(['1'])
      .pillsDataPopulated()
      .build();
    const done = assert.async();

    await render(hbs`
    <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
    </div>
    `);

    await leaveNewPillTemplate();

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    const pills = findAll(PILL_SELECTORS.queryPill);
    doubleClick(`#${pills[0].id}`, true);

    return settled().then(async() => {
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'should have no focused pill');
      await click(PILL_SELECTORS.meta);
      await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
      return settled().then(() => {
        assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
        done();
      });
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

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await click(`#${metas[1].id}`); // make it selected

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Two selecteded pills.');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Should be two pills plus template.');

    await leaveNewPillTemplate();
    doubleClick(PILL_SELECTORS.queryPill);

    return settled().then(async() => {
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

    await leaveNewPillTemplate();

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
    await settled();
    assert.equal(openGuidedPillForEditSpy.callCount, 1, 'The openGuidedPillForEditSpy pill action creator was called once');
    assert.equal(findAll(PILL_SELECTORS.activePills).length, 2, 'Now two active pills');

    doubleClick(`#${pills[1].id}`); // attempt to open another pill for edit
    await settled();
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

    return settled().then(async() => {
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

  test('clicking a complex pill should give it focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await leaveNewPillTemplate();
    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 1, 'A complex pill should be present');
    await click(PILL_SELECTORS.complexPill);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'A complex pill should be focused');

  });

  test('Right clicking on a selected pill should trigger contextMenu event AND not trigger the same when not selected', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(3);
    const done = assert.async();
    let count = 0;
    contextEventListenerCallback = () => {
      assert.ok('called when right clicked on a selected pill');
      count++;
      document.removeEventListener('contextmenu', contextEventListenerCallback);
    };
    document.addEventListener('contextmenu', contextEventListenerCallback);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected

    triggerEvent(PILL_SELECTORS.selectedPill, 'contextmenu', e);

    return settled().then(() => {
      // right click on a un-selected pill( the 2nd one), should not trigger contextMenu event
      triggerEvent(PILL_SELECTORS.expensivePill, 'contextmenu', e);
      assert.equal(findAll('.content-context-menu').length, 1, 'one menu');
      assert.equal(count, 1, 'Should be called once');
      done();
    });
  });

  test('Right clicking on a selected pill will open a context menu with 3 options', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    contextEventListenerCallback = () => {
      document.removeEventListener('contextmenu', contextEventListenerCallback);
    };
    document.addEventListener('contextmenu', contextEventListenerCallback);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
        {{context-menu}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await triggerEvent(find(PILL_SELECTORS.selectedPill), 'contextmenu', { clientX: 100, clientY: 100 });

    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 3);
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Number of pills present');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'One selecteded pill.');
    });
  });

  test('Pressing Delete key once a pill is focused will delete it', async function(assert) {
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill focused and selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Should be one pill plus template.');
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Focused pill should be deleted.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing Backspace key once a pill is focused will delete it', async function(assert) {
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill focused and selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Should be one pill plus template.');
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Focused pill should be deleted.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing Delete key once a complex pill is focused will delete it', async function(assert) {
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    await click(PILL_SELECTORS.complexPill); // make the complex pill focused and selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'Should be no complex pill');
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Focused pill should be deleted.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing backspace key once a complex pill is focused will delete it', async function(assert) {
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    await click(PILL_SELECTORS.complexPill); // make the complex pill focused and selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'Should be no complex pill');
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Focused pill should be deleted.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
    });
  });

  test('Pressing Delete key on a focused pill which is not selected, will delete only that pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(6);
    const done = assert.async();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    let metas = findAll(PILL_SELECTORS.meta);
    // select and add focus on the first pill, which is a = x
    await click(`#${metas[0].id}`);

    metas = findAll(PILL_SELECTORS.meta);
    // select and add focus on the second pill
    await click(`#${metas[1].id}`);

    metas = findAll(PILL_SELECTORS.meta);
    // click on the first pill again to remove selection, but keep it focused
    await click(`#${metas[0].id}`);

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Should be one pill plus template.');
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Focused pill should be deleted.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
      const pillText = find(PILL_SELECTORS.queryPill).title;
      assert.equal(pillText, 'b = \'y\'', 'Pill that was selected, but not focused, is still there');
      done();
    });
  });

  test('Pressing delete key on a focused and selected pill will delete that pill and the rest of the selected pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(5);
    const done = assert.async();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    let metas = findAll(PILL_SELECTORS.meta);
    // select and add focus on the first pill, which is a = x
    await click(`#${metas[0].id}`);

    metas = findAll(PILL_SELECTORS.meta);
    // select and add focus on the second pill
    await click(`#${metas[1].id}`);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Should be 2 selected pills');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 1, 'Should be just the template.');
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'No selected pill should be present.');
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'No focused pill should be present');
      done();
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

  test('Pressing ENTER key once a pill is focused will open it for edit', async function(assert) {
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    let metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected/focused
    metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // click again to keep focus but deselect

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
      assert.equal(findAll(PILL_SELECTORS.activeQueryPill).length, 2, '1 active pill and 1 new pill trigger(active)should be present');
      assert.equal(findAll(PILL_SELECTORS.pillOpenForEdit).length, 1, 'should have 1 pill open for editing');
    });
  });

  test('Pressing ENTER key once a complex pill is focused will open it for edit', async function(assert) {
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    await click(PILL_SELECTORS.complexPill); // make the complex pill selected
    await click(PILL_SELECTORS.complexPill); // click again to keep focus but deselect

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no pill selected.');
      assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');
      assert.equal(findAll(PILL_SELECTORS.complexPillActive).length, 1, 'active complex pill should be present');
      assert.equal(findAll(PILL_SELECTORS.complexPillInput).length, 1, 'complex pill input should be present');
    });
  });

  test('Pressing ENTER when no pill is focused, but selected, will not open any pill for edit', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='outside'></div>
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill selected/focused
    await click(`#${metas[1].id}`); // make the second pill selected/focused

    await click('.outside'); // removing focus from the last pill

    await triggerKeyEvent(PILL_SELECTORS.selectedPill, 'keydown', ENTER_KEY);
    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Should be still 2 pills selected, enter will not open them for edit');
    });
  });

  test('while a pill is being edited, you cannot select another pill', async function(assert) {
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

    await leaveNewPillTemplate();
    const pills = findAll(PILL_SELECTORS.queryPill);
    doubleClick(`#${pills[0].id}`); // open pill for edit
    await click(`#${pills[1].id}`); // attempt to select another pill

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no selected pills.');
  });

  test('Pressing Shift and Right arrow key once a pill is focused will select all pills to the right', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(7);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill focused and selected

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be 1 pill focused.');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY, modifiers);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Should be 2 pills selected.');
      assert.equal(selectAllPillsTowardsDirectionSpy.callCount, 1, 'The select all pills to its right action creator was called once');
      assert.equal(selectAllPillsTowardsDirectionSpy.args[0][0], 0, 'The action creator was called with the right arguments');
      assert.equal(selectAllPillsTowardsDirectionSpy.args[0][1], 'right', 'The action creator was called with the right direction arg');
    });
  });

  test('Pressing Shift and Left arrow key once a pill is focused will select all pills to the Left', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(7);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[1].id}`); // make the 2nd pill focused and selected
    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be 1 pill focused.');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY, modifiers);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Should be 2 pills selected.');
      assert.equal(selectAllPillsTowardsDirectionSpy.callCount, 1, 'The select all pills to its left action creator was called once');
      assert.equal(selectAllPillsTowardsDirectionSpy.args[0][0], 1, 'The action creator was called with the right arguments');
      assert.equal(selectAllPillsTowardsDirectionSpy.args[0][1], 'left', 'The action creator was called with the right direction arg');
    });
  });

  test('Pressing shift and right arrow key once a pill is focused, and not selected, will select all pills to its right, including itself', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(7);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0, 'No focus holder should be present');

    let metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make the 1st pill focused and selected
    metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // keep it focused, remove selection

    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be 1 pill focused.');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY, modifiers);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Should be 2 pills selected.');
      assert.equal(selectAllPillsTowardsDirectionSpy.callCount, 1, 'The select all pills to its right action creator was called once');
      assert.equal(selectAllPillsTowardsDirectionSpy.args[0][0], 0, 'The action creator was called with the right arguments');
      assert.equal(selectAllPillsTowardsDirectionSpy.args[0][1], 'right', 'The action creator was called with the right direction arg');
    });
  });

  test('Editing a complex pill sends a message to edit the pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await leaveNewPillTemplate();

    let pills = findAll(PILL_SELECTORS.complexPill);
    doubleClick(`#${pills[0].id}`); // open pill for edit
    await settled();

    // new ID, get them again
    pills = findAll(PILL_SELECTORS.complexPill);
    const inputId = `#${pills[0].id} input`;
    const newValue = 'jdsal;jdlaskjdlkas';
    await fillIn(inputId, newValue);
    await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ENTER_KEY);

    // action to store in state called
    assert.equal(editGuidedPillSpy.callCount, 1, 'The edit pill action creator was called once');
    const [ [ calledWith ] ] = editGuidedPillSpy.args;
    assert.equal(
      calledWith.pillData.complexFilterText,
      newValue,
      'The edit pill action creator was with the right text'
    );
  });

  test('Pressing escape once you have selected a pill should de-select all pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    assert.expect(2);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    await leaveNewPillTemplate();
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`);
    await click(`#${metas[1].id}`);
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'Should be 2 pills selected.');

    // Clicking ESC while focus is anywhere in the browser will deselect all pills
    await triggerKeyEvent(window, 'keydown', ESCAPE_KEY);

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be 2 pills selected.');
    });
  });

  test('Right clicking on a selected pill and choosing execute query same tab should remove deselected pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    const done = assert.async();
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);

    this.set('executeQuery', () => {
      done();
    });

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true executeQuery=executeQuery}}
        {{context-menu}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await triggerEvent(find(PILL_SELECTORS.selectedPill), 'contextmenu', { clientX: 100, clientY: 100 });

    return settled().then(async() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      await click(`#${items[0].id}`); // execute query in same tab option
      return settled().then(() => {
        assert.equal(deleteActionSpy.callCount, 1, 'The delete pill action creator was called once');
        assert.deepEqual(
          deleteActionSpy.args[0][0],
          {
            pillData: [{
              id: '2',
              meta: 'b',
              operator: '=',
              value: '\'y\'',
              isSelected: false,
              complexFilterText: undefined,
              isEditing: false,
              isInvalid: false,
              isFocused: false,
              type: 'query'
            }]
          },
          'The action creator was called with the right arguments'
        );
        assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Number of pills present');
        assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'zero selected pills');
      });
    });
  });

  test('Right clicking on a selected pill and choosing execute query new tab option should deselect all pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);

    this.set('executeQuery', () => {
      assert.ok(true);
    });

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
        {{context-menu}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'One selected pill');
    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Number of pills present');
    await triggerEvent(find(PILL_SELECTORS.selectedPill), 'contextmenu', { clientX: 100, clientY: 100 });

    return settled().then(async() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      await click(`#${items[1].id}`); // execute query in new tab option
      assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Number of pills present'); // should have the same numner of pills present
      assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'zero selected pills'); // but no selected pill
    });
  });

  test('Right clicking on a selected pill and choosing the delete selected pills option should remove selected pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
        {{context-menu}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // make it selected
    await triggerEvent(find(PILL_SELECTORS.selectedPill), 'contextmenu', { clientX: 100, clientY: 100 });

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, 'Number of pills present before deletion');

    return settled().then(async() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      await click(`#${items[2].id}`); // delete option
      return settled().then(() => {
        assert.equal(deleteSelectedGuidedPillsSpy.callCount, 1, 'The delete selected pill action creator was called once');
        assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'Number of pills present');
        assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'zero selected pills');
      });
    });
  });

  test('Clicking an inactive pill switches focus from any other pill that has focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();
    const done = assert.async();
    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();
    let metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // select the first pill

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    const pillText = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillText, 'a = \'x\'', 'Focused expected on the first pill');

    metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[1].id}`); // select the second pill

    return settled().then(async() => {
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should still have 1 focused pill');
      const pillText = find(PILL_SELECTORS.focusedPill).title;
      assert.equal(pillText, 'b = \'y\'', 'Focused expected on the first pill');
      done();
    });
  });

  test('If a pill is opened for edit and submitted, it should get focus', async function(assert) {
    assert.expect(2);
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'there should be no focused pill');

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    });
  });

  test('If a pill is opened for edit and escaped, it should get focus', async function(assert) {
    assert.expect(2);
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'there should be no focused pill');

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ESCAPE_KEY);
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    });
  });

  test('Clicking anywhere on the new pill trigger or the new pill template should remove focus from any pill', async function(assert) {
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

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`); // add focus to the first pill


    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'there should be 1 focused pill');
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'should have no focused pill');
  });

  test('Deleting a pill removes focus from any pill that has it', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true }}`);
    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[1].id}`); // add focus to the second pill

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');

    await click(PILL_SELECTORS.deletePill); // delete the first pill

    return settled().then(async() => {
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'should have no focused pill');
    });
  });

  test('Pressing escape twice when you have a selected and focused pill should remove focus', async function(assert) {
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
    await leaveNewPillTemplate();
    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`);
    await click(`#${metas[1].id}`);
    // Two pills are selected, one is focused
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be just 1 pill focused');
    // Deselect all pills.
    await triggerKeyEvent(window, 'keydown', ESCAPE_KEY);
    // Test that there are no pills selected, but one focused
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'Should be no selected pill');
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be one pill focused');
    // Remove focus
    await triggerKeyEvent(window, 'keydown', ESCAPE_KEY);
    // Test that there are no focused pills
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Should be no pill focused');
  });

  test('ComplexPill - Pressing escape when you have a focused pill should remove focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    await leaveNewPillTemplate();
    await click(PILL_SELECTORS.complexPill);
    // One pill is selected and focused
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'Should be just 1 pill focused');
    // Deselect all pills.
    await triggerKeyEvent(window, 'keydown', ESCAPE_KEY);
    // Remove focus
    await triggerKeyEvent(window, 'keydown', ESCAPE_KEY);

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'Should be no pill focused');
  });

  skip('ComplexPill - Pressing escape from an edit should leave focus on that pill, pressing it again should remove focus', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataComplex()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'there should be no focused pill');

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    doubleClick(PILL_SELECTORS.complexPill, false);
    return settled().then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ESCAPE_KEY);
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
      await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ESCAPE_KEY);
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'should have no focused pill');
    });

  });

  test('Pressing escape from an edit should leave focus on that pill, pressing it again should remove focus', async function(assert) {
    assert.expect(3);
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

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'there should be no focused pill');

    // pass flag to skip extra events because they fire when they
    // shouldn't as dispatchEvent is sync
    doubleClick(PILL_SELECTORS.queryPill, true);

    return settled().then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ESCAPE_KEY);
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
      await triggerKeyEvent(PILL_SELECTORS.queryPill, 'keydown', ESCAPE_KEY);
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'should have no focused pill');
    });
  });

  test('Right clicking anywhere on the window dom should remove focus(if any) from a pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      <div class='outside'></div>
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    const metas = findAll(PILL_SELECTORS.meta);
    await click(`#${metas[0].id}`);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    await triggerEvent(find('.outside'), 'click', { button: 2, ctrlKey: true });

    return settled().then(() => {
      assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 0, 'should have no focused pill');
    });
  });

  test('Creating a pill and hitting enter should remove focus from meta drop-down', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .pillsDataEmpty()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);
    await createBasicPill();

    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');

    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ENTER_KEY);

    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 0, 'Should not have a drop-down anymore');
  });

  test('Focus moves to left pill if ARROW-LEFT is pressed from a new pill(end of list) with no meta/operator/value selected', async function(assert) {
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

    await leaveNewPillTemplate();
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    const pillText = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillText, 'b = \'y\'', 'The second pill is the focused pill');
  });

  test('Nothing happens if ARROW-RIGHT is pressed from a new pill(end of the list) with no meta/operator/value selected with no pill on the right', async function(assert) {
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
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');
  });

  test('Focus moves to left pill if ARROW-LEFT is pressed from a new pill(in between pills) with no meta/operator/value selected', async function(assert) {
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

    await leaveNewPillTemplate();
    const triggers = findAll(PILL_SELECTORS.newPillTrigger);

    // click on the second trigger to place cursor in between the 2 pills
    await click(triggers[1]);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 0, 'Should not have a meta drop-down available');

    // The first pill should now be focused
    const pillText = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillText, 'a = \'x\'', 'The first pill is the focused pill');
  });

  test('Focus moves to right pill if ARROW-RIGHT is pressed from a new pill(in between pills) with no meta/operator/value selected', async function(assert) {
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

    await leaveNewPillTemplate();
    const triggers = findAll(PILL_SELECTORS.newPillTrigger);

    // click on the second trigger to place cursor in between the 2 pills
    await click(triggers[1]);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'should have 1 focused pill');
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 0, 'Should not have a meta drop-down available');

    // The second pill should now be focused
    const pillText = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillText, 'b = \'y\'', 'The second pill is the focused pill');
  });

  test('Nothing happens if ARROW-LEFT is pressed from a new pill(start of the list) with no meta/operator/value selected with no pill on the left', async function(assert) {
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

    await leaveNewPillTemplate();
    const triggers = findAll(PILL_SELECTORS.newPillTrigger);

    // click on the first trigger to place cursor at the very start of the list
    await click(triggers[0]);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');
  });

  test('Navigate from right most pill to the start of the list by pressing ARROW_LEFT', async function(assert) {
    assert.expect(7);
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

    await leaveNewPillTemplate();
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'The second(last) pill should be focused');
    const pillTextOne = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillTextOne, 'b = \'y\'', 'The second pill is the focused pill');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'The first pill should be focused');
    const pillTextTwo = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillTextTwo, 'a = \'x\'', 'The first pill is the focused pill');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Nothing should happen.Should still have a meta drop-down available');
  });

  test('Navigate from left most pill to the end of the list by pressing ARROW_RIGHT', async function(assert) {
    assert.expect(8);
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

    await leaveNewPillTemplate();
    await focus(PILL_SELECTORS.triggerMetaPowerSelect);

    const triggers = findAll(PILL_SELECTORS.newPillTrigger);
    // click on the first trigger to place cursor at the very start of the list
    await click(triggers[0]);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'The first pill should be focused');
    const pillTextTwo = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillTextTwo, 'a = \'x\'', 'The first pill is the focused pill');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have a meta drop-down available');

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.focusedPill).length, 1, 'The second(last) pill should be focused');
    const pillTextOne = find(PILL_SELECTORS.focusedPill).title;
    assert.equal(pillTextOne, 'b = \'y\'', 'The second pill is the focused pill');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Should have the new pill template meta open');

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectDropdown).length, 1, 'Nothing should happen.Should still have the new pill template meta open');
  });

  test('Meta pill can create a free-form pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    // enter non-meta value for pill and press ENTER
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('(foobar)');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
    await settled();
    // assert.equal(addFreeFormFilterSpy.callCount, 1, 'The add free form filter creator was called once');
    // assert.deepEqual(
    //   addFreeFormFilterSpy.args[0][0],
    //   { freeFormText: '(foobar)', position: 0, shouldAddFocusToNewPill: false, fromFreeFormMode: false, shouldForceComplex: true },
    //   'The action creator was called with the right arguments'
    // );
    assert.equal(find(PILL_SELECTORS.complexPill).getAttribute('title'), '(foobar)', 'Expected stringified pill');
  });

  test('Meta pill can create a text pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);

    // enter non-meta value for pill and press ENTER
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('foo-bar-baz');
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_LABEL));
    await click(textFilter);
    await settled();
    // assert.equal(addTextFilterSpy.callCount, 1, 'The add text filter creator was called once');
    // assert.deepEqual(
    //   addTextFilterSpy.args[0][0],
    //   { searchTerm: 'foo-bar-baz', position: 0, shouldAddFocusToNewPill: false, fromFreeFormMode: false, shouldForceComplex: false },
    //   'The action creator was called with the right arguments'
    // );
    assert.equal(find(PILL_SELECTORS.textPill).getAttribute('title'), 'foo-bar-baz', 'Expected stringified pill');
  });

  test('it should not have Advanced Options in the afterOptionsComponent if a pill is open for edit', async function(assert) {
    assert.expect(4);
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

    // Should be able to see Advanced Options on a new pill trigger/meta
    assert.ok(find(PILL_SELECTORS.powerSelectAfterOptions), 'Should be able to see Advanced Options in meta component');

    // select some meta and move to operator comp
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // select some meta and move to operator comp
    // Should be able to see Advanced Options on operator dropdown
    assert.ok(find(PILL_SELECTORS.powerSelectAfterOptions), 'Should be able to see Advanced Options in operator component');

    // Select an operator and move to value comp
    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');
    // Should be able to see the Advanced Options
    assert.ok(find(PILL_SELECTORS.powerSelectAfterOptions), 'Should be able to see Advanced Options in value component');

    // Remove all focus
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ESCAPE_KEY);

    // Open a pill for edit
    await doubleClick(PILL_SELECTORS.queryPill, true);

    // But while editing, there should not be Advanced Options in the dropdown
    assert.notOk(find(PILL_SELECTORS.powerSelectAfterOptions), 'Should be no Advanced Options in value component');

  });

  test('editing a guided pill to complex and hitting enter should still make it guided', async function(assert) {
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
    await leaveNewPillTemplate();

    // Open a pill for edit
    await doubleClick(PILL_SELECTORS.queryPill, true);
    await settled();

    const pills = findAll(PILL_SELECTORS.queryPill);

    const inputId = `#${pills[0].id} input`;
    const newValue = 'x && sessionid exists';
    await fillIn(inputId, newValue);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);

    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'There should be no complex pill from a guided pill edit');
  });

  test('it should have pill tabs in each of the power selects', async function(assert) {

    const assertTabContents = (assert) => {
      assert.ok(find(PILL_SELECTORS.pillTabs), 'Should be able to see tabs in current component');
      assert.ok(find(PILL_SELECTORS.metaTab), 'Should be able to see tabs in current component');
      assert.ok(find(PILL_SELECTORS.recentQueriesTab), 'Should be able to see tabs in current component');
    };
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

    // Should be able to see pill tabs
    assertTabContents(assert);

    // select some meta and move to operator comp
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // select some meta and move to operator comp
    // Should be able to see tabs on operator dropdown
    assertTabContents(assert);

    // Select an operator and move to value comp
    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');
    // Should be able to see tabs on value dropdown
    assertTabContents(assert);

    // Remove all focus
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ESCAPE_KEY);

    // Open a pill for edit
    await doubleClick(PILL_SELECTORS.queryPill, true);

    // But while editing, there should not be tabs in the dropdown
    assert.notOk(find(PILL_SELECTORS.pillTabs), 'Should be no tabs while editing');

  });

  test('it should have pill tabs and clicking on a tab selects it', async function(assert) {

    const assertTabContents = (assert, metaTab, recentQueriesTab) => {
      assert.ok(find(PILL_SELECTORS.pillTabs), 'Should be able to see tabs in current component');
      assert.ok(find(metaTab), 'Should be able find metaTab component');
      assert.ok(find(recentQueriesTab), 'Should be able to find recentQueries component');
    };
    assert.expect(6);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .recentQueriesUnfilteredList()
      .recentQueriesFilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    // Should be able to see pill tabs with meta selected
    assertTabContents(assert, PILL_SELECTORS.metaTabSelected, PILL_SELECTORS.recentQueriesTab);
    await click(PILL_SELECTORS.recentQueriesTab);
    // Should be able to see pill tabs with recent-queries selected
    assertTabContents(assert, PILL_SELECTORS.metaTab, PILL_SELECTORS.recentQueriesTabSelected);
  });

  test('Power-select drop down appears after creating text filter from pill-meta', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();
    await render(hbs`
      {{query-container/query-pills
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('jazzy');
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_LABEL));
    await click(textFilter);
    await settled();
    assert.ok(find(PILL_SELECTORS.textPill), 'text pill was not created');
    assert.ok(find(PILL_SELECTORS.powerSelectDropdown), 'power-select dropdown not rendered');
  });

  test('Power-select drop down appears after creating text filter from pill-operator', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();
    await render(hbs`
      {{query-container/query-pills
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // option A
    await typeInSearch('jazzy');
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_LABEL));
    await click(textFilter);
    await settled();
    assert.ok(find(PILL_SELECTORS.textPill), 'text pill was not created');
    assert.ok(find(PILL_SELECTORS.textPill).textContent, 'a jazzy', 'text pill has incorrect text');
    assert.ok(find(PILL_SELECTORS.powerSelectDropdown), 'power-select dropdown not rendered');
  });

  test('An invalid pill should remain invalid if ESCAPEed during edit', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .invalidPillsDataPopulated()
      .build();
    await render(hbs`{{query-container/query-pills isActive=true}}`);
    await leaveNewPillTemplate();
    assert.ok(find(PILL_SELECTORS.invalidPill), 'invalid pill was not created');
    await doubleClick(PILL_SELECTORS.queryPill, true);
    assert.ok(find(PILL_SELECTORS.pillOpenForEdit), 'pill was not opened for editing');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ESCAPE_KEY);
    assert.ok(find(PILL_SELECTORS.invalidPill), 'invalid pill should have remained invalid');
  });

  test('pills tabs should work the same way for new-pill-trigger', async function(assert) {

    const assertTabContents = (assert) => {
      assert.ok(find(PILL_SELECTORS.pillTabs), 'Should be able to see tabs in current component');
      assert.ok(find(PILL_SELECTORS.metaTab), 'Should be able to see tabs in current component');
      assert.ok(find(PILL_SELECTORS.recentQueriesTab), 'Should be able to see tabs in current component');
    };
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.newPillTrigger);

    await click(PILL_SELECTORS.recentQueriesTab);
    // Should be able to see pill tabs with recent-queries selected
    assertTabContents(assert, PILL_SELECTORS.metaTab, PILL_SELECTORS.recentQueriesTabSelected);

    await click(PILL_SELECTORS.metaTab);
    // Should be able to see pill tabs with meta selected
    assertTabContents(assert, PILL_SELECTORS.metaTabSelected, PILL_SELECTORS.recentQueriesTab);
  });

  test('Text typed in recent query tab calls its action creator', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 's');
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);
    await settled();

    assert.ok(recentQueriesSpy.calledOnce, 'The recent query creator was not called once');
    assert.propEqual(recentQueriesSpy.args[0][0],
      's',
      'The recent query creator was returned the wrong arguments');

  });

  skip('Spinner, when recent query call is in progress,  and No results message will never be together', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');
    assert.equal(findAll(PILL_SELECTORS.loadingSpinnerSelector).length, 0, 'Found power-select options loading spinner when we should not have');

    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'xli');

    await waitUntil(() => findAll(PILL_SELECTORS.loadingSpinnerSelector).length === 1 && find(PILL_SELECTORS.noResultsMessageSelector).textContent.trim() === '', { timeout: 5000 });
    await waitUntil(() => find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent') && find(PILL_SELECTORS.loadingSpinnerSelector).getAttribute('style') === 'display: none;', { timeout: 5000 });

  });

  skip('Should see a spinner in power-select options when recentQueriesCallInProgress is true', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'xli');
    const state = this.owner.lookup('service:redux').getState();

    const { investigate: { queryNode: { recentQueriesCallInProgress } } } = state;
    assert.ok(recentQueriesCallInProgress, 'recentQueriesCallInProgress was not changed');
    assert.equal(findAll(PILL_SELECTORS.loadingSpinnerSelector).length, 1, 'Did not find the power-select options loading spinner');
  });

  skip('When text is present, filtered List is displayed in recent-query', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'medium');
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);

    await waitUntil(() => findAll(PILL_SELECTORS.loadingSpinnerSelector).length === 1);
    await waitUntil(() => find(PILL_SELECTORS.loadingSpinnerSelector).getAttribute('style') === 'display: none;', { timeout: 5000 });

    const selectorArray = findAll(PILL_SELECTORS.recentQueriesOptions);
    const optionsArray = selectorArray.map((el) => el.textContent);
    const state = this.owner.lookup('service:redux').getState();
    const { investigate: { queryNode: { recentQueriesFilteredList } } } = state;
    const filteredList = recentQueriesFilteredList.map((ob) => ob.query);
    assert.deepEqual(filteredList, optionsArray, 'There is a mis-match in the options displayed');

  });

  skip('Typing 1 char and backspacing should switch between filtered and unfiltered list', async function(assert) {
    new ReduxDataHelper(setState)
      .canQueryGuided()
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'm');
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);

    await waitUntil(() => findAll(PILL_SELECTORS.loadingSpinnerSelector).length === 1, { timeout: 10000 });
    await waitUntil(() => find(PILL_SELECTORS.loadingSpinnerSelector).getAttribute('style') === 'display: none;', { timeout: 10000 });

    const state = this.owner.lookup('service:redux').getState();
    const { investigate: { queryNode: { recentQueriesUnfilteredList, recentQueriesFilteredList } } } = state;
    const selectorArray = findAll(PILL_SELECTORS.recentQueriesOptions);
    const optionsArray = selectorArray.map((el) => el.textContent);
    const filteredList = recentQueriesFilteredList.map((ob) => ob.query);
    assert.deepEqual(filteredList, optionsArray, 'There is a mis-match in the options displayed');

    // Enact a backspace
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, ' ');

    const selectorArrayUnfiltered = findAll(PILL_SELECTORS.recentQueriesOptions);
    const optionsArrayUnfiltered = selectorArrayUnfiltered.map((el) => el.textContent);
    const unfilterdList = recentQueriesUnfilteredList.map((ob) => ob.query);
    assert.deepEqual(unfilterdList, optionsArrayUnfiltered, 'There is a mis-match in the options displayed');
  });

  test('Closing power-select drop-down removes spinner from dom', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'xli');
    await waitUntil(() => findAll(PILL_SELECTORS.loadingSpinnerSelector).length === 1);

    await triggerKeyEvent(PILL_SELECTORS.recentQueryTrigger, 'keydown', ESCAPE_KEY);
    assert.equal(findAll(PILL_SELECTORS.loadingSpinnerSelector).length, 0, 'Found loading spinner in dom when it should not have');

  });

  test('Typing text in pill-meta will reflect a count change in tabs', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .canQueryGuided()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await typeIn(PILL_SELECTORS.metaSelectInput, 'mediu');

    await settled();


    assert.equal(find(PILL_SELECTORS.metaCount).textContent, '(1)', 'Meta tab count is incorrect');
    setTimeout(() => {
      assert.equal(find(PILL_SELECTORS.recentQueryCount).textContent, '(3)', 'recent query tab count is incorrect');
      done();
    }, 10000);
  });

  test('Typing text in pill-operator will reflect a count change in tabs', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .canQueryGuided()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.meta, 'sessionid');

    await typeIn(PILL_SELECTORS.operatorSelectInput, '=');

    await settled();


    assert.equal(find(PILL_SELECTORS.metaCount).textContent, '(1)', 'Meta tab count is incorrect');
    setTimeout(() => {
      assert.equal(find(PILL_SELECTORS.recentQueryCount).textContent, '(1)', 'recent query tab count is incorrect');
      done();
    }, 10000);
  });

  test('Typing text in pill-value will reflect a count change in tabs', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .canQueryGuided()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.meta, 'medium');

    await selectChoose(PILL_SELECTORS.operator, '=');

    await typeIn(PILL_SELECTORS.valueSelectInput, '32 || med');

    await settled();


    assert.equal(find(PILL_SELECTORS.metaCount).textContent, '(1)', 'Meta tab count is incorrect');
    setTimeout(() => {
      assert.equal(find(PILL_SELECTORS.recentQueryCount).textContent, '(1)', 'recent query tab count is incorrect');
      done();
    }, 10000);
  });

  test('Typing text in recent-query will reflect a count change in tabs', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .canQueryGuided()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'medium = 32 ||');

    await settled();


    assert.equal(find(PILL_SELECTORS.metaCount).textContent, '(1)', 'Meta tab count is incorrect');
    setTimeout(() => {
      assert.equal(find(PILL_SELECTORS.recentQueryCount).textContent, '(1)', 'recent query tab count is incorrect');
      done();
    }, 5000);
  });

  test('Backspacing all the way to the end will reset the tab counts', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .canQueryGuided()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'sessionid = 1');

    await settled();


    assert.equal(find(PILL_SELECTORS.metaCount).textContent, '(1)', 'Meta tab count is incorrect');
    setTimeout(async() => {
      assert.equal(find(PILL_SELECTORS.recentQueryCount).textContent, '(1)', 'recent query tab count is incorrect');
      await fillIn(PILL_SELECTORS.recentQuerySelectInput, ' ');
      setTimeout(() => {
        assert.equal(find(PILL_SELECTORS.metaCount).textContent, '(0)', 'Meta tab count is incorrect');
        assert.equal(find(PILL_SELECTORS.recentQueryCount).textContent, '(0)', 'recent query tab count is incorrect');
        done();
      }, 5000);
    }, 5000);
  });

  test('Selecting a pill from recent query dropdown broadcasts a message with correct args', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .canQueryGuided()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-pills isActive=true}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await selectChoose(PILL_SELECTORS.recentQuery, 'medium = 32');

    assert.ok(batchAddQueriesSpy.calledOnce, 'Batch pills creator was not called once');
    assert.propEqual(batchAddQueriesSpy.args[0][0],
      {
        'initialPosition': 0,
        pillsData: [
          {
            meta: 'medium',
            operator: '=',
            type: 'query',
            value: '32'
          }
        ]
      },
      'The creator was called with the wrong arguments');
    done();

  });

  test('Entering an open paren will insert a pair of parens', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();
    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', '(');
    assert.ok(find(PILL_SELECTORS.openParen), 'Missing open paren');
    assert.ok(find(PILL_SELECTORS.closeParen), 'Missing close paren');
  });

  test('Can create a new pill inside a pair of parens', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();
    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', '(');
    assert.ok(find(PILL_SELECTORS.metaInput), 'Missing active/open meta');
    // Create a pill
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);
    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');
    await typeIn(PILL_SELECTORS.valueSelectInput, 'b');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
    // Test that new pill is in between open and close parens, and is focused.
    const items = document.querySelectorAll('.query-pills > div');
    // NPT, OP, NPT, Pill, NPT, CP, NPT
    assert.equal(items.length, 7, 'Incorrect number of query items');
    assert.equal(trim(items[1].textContent), '(', 'Should be an open paren');
    assert.equal(trim(items[3].textContent), 'a=\'b\'', 'Should be correct pill text');
    assert.ok(items[3].getAttribute('class').includes('is-focused'), 'Should be focused');
    assert.equal(trim(items[5].textContent), ')', 'Should be a close paren');
  });

  test('new pill triggers render appropriately when including parens', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`{{query-container/query-pills isActive=true}}`);
    assert.equal(findAll(PILL_SELECTORS.newPillTriggerContainer).length, 3, 'There should three new pill triggers.');
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 1, 'found 1 new pill template focused');
  });

  test('Focus traverses guided pills and parens', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    // the last (4th) NPT should be focused to start
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 1, 'found 1 new pill template focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 0, 'close paren should not be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 0, 'guided pill should not be focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 0, 'open paren should not be focused');

    // now the close paren should be focused
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ARROW_LEFT_KEY);
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 0, 'no pill templates focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 1, 'close paren should be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 0, 'guided pill should not be focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 0, 'open paren should not be focused');

    // now the 3rd NPT should be focused
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ARROW_LEFT_KEY);
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 1, 'found 1 new pill template focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 0, 'close paren should not be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 0, 'guided pill should not focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 0, 'open paren should not be focused');

    // now the guided pill should be focused
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ARROW_LEFT_KEY);
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 0, 'no new pill template focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 0, 'close paren should not be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 1, 'guided pill should be focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 0, 'open paren should not be focused');

    // now the 2nd NPT should be focused
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ARROW_LEFT_KEY);
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 1, 'found 1 new pill template focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 0, 'close paren should not be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 0, 'guided pill should not focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 0, 'open paren should not be focused');

    // now the open paren should be focused
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ARROW_LEFT_KEY);
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 0, 'no pill templates focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 0, 'close paren should not be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 0, 'guided pill should not be focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 1, 'open paren should be focused');

    // now the 1st NPT  should be focused
    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', ARROW_LEFT_KEY);
    assert.ok(findAll(PILL_SELECTORS.metaInputFocused), 1, 'found 1 new pill template focused');
    assert.ok(findAll(PILL_SELECTORS.closeParenFocused), 0, 'close paren should not be focused');
    assert.ok(findAll(PILL_SELECTORS.focusedQueryPill), 0, 'guided pill should not focused');
    assert.ok(findAll(PILL_SELECTORS.openParenFocused), 0, 'open paren should not be focused');
  });

  test('Can select multiple parens', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'found 0 new pill template selected');

    await click(PILL_SELECTORS.closeParen);
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 2, 'found 2 paren selected, including the twin of the one clicked');

    await click(PILL_SELECTORS.closeParen);
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 0, 'found 0 paren selected, which means the twin was deslected as well');
  });

  test('Typing ")" when there is a close paren to the right will move focus to right', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await leaveNewPillTemplate();

    // NPT, (, NPT, pill, NPT, ), new pill template
    const triggers = findAll(PILL_SELECTORS.newPillTrigger);
    assert.equal(triggers.length, 3, 'should be three triggers');
    // focus on NPT to the right of the pill
    triggers[2].click();

    await triggerKeyEvent(PILL_SELECTORS.metaInput, 'keydown', CLOSE_PAREN_KEY);
    assert.notOk(findAll('.new-pill-trigger input').length, 'The no new-pill-triggers are open for input');
    assert.equal(find('.new-pill-template input').value, '', 'The new-pill-template does not have a close paren in it');
  });

  test('Typing ")" when there are an uneven number of open/close parens will insert ")("', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await leaveNewPillTemplate();

    // NPT, (, NPT, pill, NPT, ), new pill template
    const triggers = findAll(PILL_SELECTORS.newPillTrigger);
    assert.equal(triggers.length, 3, 'should be three triggers');

    // focus on NPT to the right of the open paren and type a ")"
    await click(triggers[1]);
    await typeIn(PILL_SELECTORS.metaInput, ')');
    // will now look like with the second "(" having focus
    // NPT, (, NPT, ), NPT, (, NPT, pill, NPT, ), new pill template
    assert.notOk(findAll('.new-pill-trigger input').length, 'the no new-pill-triggers are open for input');
    assert.equal(findAll(PILL_SELECTORS.newPillTrigger).length, 5, 'should be 5 new-pill-triggers');
    assert.ok(find(PILL_SELECTORS.openParenFocused), 'there should be an open paren with focus');
  });

  test('pill-value will have options in the drop-down', async function(assert) {
    assert.expect(1);
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'contains');

    await waitUntil(() => findAll(PILL_SELECTORS.powerSelectOption).length > 1, { timeout: 5000 }).then(() => {
      assert.ok(findAll(PILL_SELECTORS.powerSelectOption).length === 11, 'Should have found default plus 10 value suggestions, but didnt');
      done();
    });

  });

  test('Will trigger valueSuggestions once meta is selected and when something is typed in pill-value', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'contains');

    await typeIn(PILL_SELECTORS.valueSelectInput, 't');

    await settled();

    assert.ok(valueSuggestionsSpy.calledTwice, 'Value suggestions spy was not called twice');
    const { firstCall, secondCall } = valueSuggestionsSpy;

    assert.equal(firstCall.args[0], 'alert', 'First call to the value suggestions action creator is incorrect');
    assert.equal(secondCall.args[0], 'alert', 'second call to the value suggestions action creator is incorrect');
    assert.equal(secondCall.args[1], 't', 'second call to the value suggestions action creator is incorrect');

  });

  test('Typing DELETE when an open paren is focused will delete both the open and closed paren', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.openParen);
    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
    assert.notOk(find(PILL_SELECTORS.openParen), 'Missing open paren');
    assert.notOk(find(PILL_SELECTORS.closeParen), 'Missing close paren');
  });

  test('Typing DELETE when an close paren is focused will delete both the open and closed paren', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await leaveNewPillTemplate();

    await click(PILL_SELECTORS.closeParen);
    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1, 'Focus holder should be present now');

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
    assert.notOk(find(PILL_SELECTORS.openParen), 'open paren is present');
    assert.notOk(find(PILL_SELECTORS.closeParen), 'close paren is present');
  });

  test('Typing DELETE when an close paren is focused will delete both the open and closed paren and any other selected pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataWithParens()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);
    await leaveNewPillTemplate();

    assert.equal(findAll(PILL_SELECTORS.meta).length, 2, '1 pill 1 open template');

    await click(PILL_SELECTORS.meta); // make the pill selected
    await click(PILL_SELECTORS.closeParen);
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);

    assert.notOk(find(PILL_SELECTORS.openParen), 'open paren is present');
    assert.notOk(find(PILL_SELECTORS.closeParen), 'close paren is present');
    assert.equal(findAll(PILL_SELECTORS.meta).length, 1, '1 pill deleted so just the template remains');
  });

  test('Can create pill using mouse clicks on value suggestions', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'contains');

    await waitUntil(() => findAll(PILL_SELECTORS.powerSelectOption).length > 1, { timeout: 5000 }).then(async() => {
      const values = findAll(PILL_SELECTORS.powerSelectOptionValue);
      await click(values[1]);

      assert.ok(newActionSpy.calledOnce, 'The addGuidedPill creator was not called once');
      done();
    });
  });

  test('Can create pill using arrow keys to highlight and press ENTER on value suggestions', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'contains');

    await waitUntil(() => findAll(PILL_SELECTORS.powerSelectOption).length > 1, { timeout: 5000 }).then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN_KEY);
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN_KEY);
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);

      assert.ok(newActionSpy.calledOnce, 'The addGuidedPill creator was not called once');
      done();
    });
  });

  test('Can edit pill using value suggestions', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataPopulated()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);

    await leaveNewPillTemplate();

    const pills = findAll(PILL_SELECTORS.meta);
    doubleClick(`#${pills[0].id}`, true); // open pill for edit
    await settled();

    await waitUntil(() => findAll(PILL_SELECTORS.powerSelectOption).length > 1, { timeout: 5000 }).then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN_KEY);
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN_KEY);
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);

      assert.ok(editGuidedPillSpy.calledOnce, 'The editGuidedPillSpy creator was not called once');
      done();
    });
  });

  test('it shows a placeholder if it is the first pill', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    await render(hbs`
      {{query-container/query-pills isActive=true}}
    `);


    const placeholder = find(PILL_SELECTORS.metaSelectInput).getAttribute('placeholder');
    assert.ok(placeholder.length > 0, 'appears to be missing a placeholder');
  });

  test('it does not show a placeholder if there is a pill present', async function(assert) {
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

    await leaveNewPillTemplate();
    const triggers = findAll(PILL_SELECTORS.newPillTrigger);

    // Click the first pill trigger
    await click(triggers[0]);

    const placeholder = find(PILL_SELECTORS.metaSelectInput).getAttribute('placeholder');
    assert.ok(placeholder.length === 0, 'Should not see a placeholder');
  });
});
