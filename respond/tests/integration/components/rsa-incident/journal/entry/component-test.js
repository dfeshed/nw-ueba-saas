import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { throwSocket } from '../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../helpers/patch-flash';
import sinon from 'sinon';
import RSVP from 'rsvp';
import { click, find, findAll, render } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import DataHelper from '../../../../../helpers/data-helper';
import editableFieldHelper from '../../../../../helpers/editable-field';
import { getAllMilestoneTypes } from 'respond/actions/creators/dictionary-creators';
import * as JournalCreators from 'respond/actions/creators/journal-creators';

let dispatchSpy, redux, setup;

const journalEntry = {
  id: '12',
  author: 'Tony',
  notes: 'Assigned to you. Please take a look.',
  created: 1483990366970,
  milestone: 'CONTAINMENT'
};

const findModal = (selector) => {
  return document.querySelector(`#modalDestination ${selector}`);
};

module('Integration | Component | Journal Entry', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');

    // initialize all of the required data into redux app state
    setup = () => {
      return RSVP.allSettled([
        redux.dispatch(getAllMilestoneTypes())
      ]);
    };
  });

  hooks.afterEach(function() {
    dispatchSpy.restore();
  });

  test('The rsa-incident/journal/entry component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-incident/journal/entry}}`);
    assert.ok(find('.rsa-incident-journal-entry'), 'The rsa-incident/journal/entry component should be found in the DOM');
  });

  test('The journal entry data is rendered as expected', async function(assert) {
    await setup();
    this.set('journalEntry', journalEntry);
    await render(hbs`{{rsa-incident/journal/entry entry=journalEntry}}`);
    assert.equal(find('.rsa-incident-journal-entry__milestone button').textContent.trim(), 'Containment', 'The milestone is displayed as expected');
    assert.equal(find('.rsa-incident-journal-entry__note .editable-field__value').textContent.trim(), 'Assigned to you. Please take a look.', 'The description is displayed as expected');
  });

  test('The delete button dispatches a deleteItem action', async function(assert) {
    const actionSpy = sinon.spy(JournalCreators, 'deleteJournalEntry');
    new DataHelper(redux).fetchIncidentDetails();
    await setup();
    this.set('journalEntry', journalEntry);
    await render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    await click('header .delete button');
    assert.ok(findModal('.respond-confirmation-dialog'), 'The confirmation dialog is showing');
    await click('.modal-footer-buttons .is-primary button');
    assert.ok(actionSpy.calledOnce, 'The deleteJournalEntry action was called once');
    actionSpy.reset();
    actionSpy.restore();
  });

  test('Updates to the description dispatches an updateJournalEntry action', async function(assert) {
    const actionSpy = sinon.spy(JournalCreators, 'updateJournalEntry');
    new DataHelper(redux).fetchIncidentDetails();
    await setup();
    this.set('journalEntry', journalEntry);
    await render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    await editableFieldHelper.updateEditableField('.rsa-incident-journal-entry__note', 'Assigned to me. Taking a look');
    assert.ok(actionSpy.calledOnce, 'The updateJournalEntry action was called once');
    actionSpy.reset();
    actionSpy.restore();
  });

  test('Changes to the milestone dispatches an updateJournalEntry action', async function(assert) {
    const actionSpy = sinon.spy(JournalCreators, 'updateJournalEntry');
    new DataHelper(redux).fetchIncidentDetails();
    await setup();
    this.set('journalEntry', journalEntry);
    await render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    await clickTrigger('.rsa-incident-journal-entry__milestone');
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 9, 'There are 9 milestone options available');
    await selectChoose('.rsa-incident-journal-entry__milestone', '.ember-power-select-option', 0);
    assert.ok(actionSpy.calledOnce, 'The updateJournalEntry action creators was called once');
    actionSpy.reset();
    actionSpy.restore();
  });

  test('An error flash message is displayed if there is an error updating journal entry', async function(assert) {
    assert.expect(2);
    const done = assert.async();
    new DataHelper(redux).fetchIncidentDetails();
    await setup();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.updateFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.set('journalEntry', journalEntry);
    await render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    throwSocket();
    await editableFieldHelper.updateEditableField('.rsa-incident-journal-entry__note', 'Assigned to me. Taking a look');
  });

  test('An error flash message is displayed if there is an error deleting the journal entry', async function(assert) {
    assert.expect(2);
    const done = assert.async();
    new DataHelper(redux).fetchIncidentDetails();
    await setup();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.deleteFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.set('journalEntry', journalEntry);
    await render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    throwSocket();
    await click('header .delete button');
    await click('.modal-footer-buttons .is-primary button');
  });
});