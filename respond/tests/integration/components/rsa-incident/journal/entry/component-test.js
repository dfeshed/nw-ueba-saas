import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import DataHelper from '../../../../../helpers/data-helper';
import editableFieldHelper from '../../../../../helpers/editable-field';
import { getAllMilestoneTypes } from 'respond/actions/creators/dictionary-creators';
import * as JournalCreators from 'respond/actions/creators/journal-creators';
import wait from 'ember-test-helpers/wait';
import { clickTrigger } from '../../../../../helpers/ember-power-select';
import triggerNativeEvent from '../../../../../helpers/trigger-native-event';
import sinon from 'sinon';
import RSVP from 'rsvp';
import $ from 'jquery';
import { throwSocket } from '../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { getOwner } from '@ember/application';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let dispatchSpy, redux, init;

const journalEntry = {
  id: '12',
  author: 'Tony',
  notes: 'Assigned to you. Please take a look.',
  created: 1483990366970,
  milestone: 'CONTAINMENT'
};

function selectOption(index) {
  const [ option ] = $('.ember-power-select-option').eq(index || 0);
  triggerNativeEvent(option, 'mouseover');
  triggerNativeEvent(option, 'mousedown');
  triggerNativeEvent(option, 'mouseup');
  triggerNativeEvent(option, 'click');
}

moduleForComponent('rsa-incident/journal/entry', 'Integration | Component | Journal Entry', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');

    // initialize all of the required data into redux app state
    init = RSVP.allSettled([
      redux.dispatch(getAllMilestoneTypes())
    ]);

    initialize(this);
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The rsa-incident/journal/entry component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-incident/journal/entry}}`);
  assert.equal(this.$('.rsa-incident-journal-entry').length, 1, 'The rsa-incident/journal/entry component should be found in the DOM');
});

test('The journal entry data is rendered as expected', function(assert) {
  return init.then(() => {
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry entry=journalEntry}}`);
    assert.equal(this.$('.rsa-incident-journal-entry__milestone button').text().trim(), 'Containment', 'The milestone is displayed as expected');
    assert.equal(this.$('.rsa-incident-journal-entry__note .editable-field__value').text().trim(), 'Assigned to you. Please take a look.', 'The description is displayed as expected');
  });
});

test('The delete button dispatches a deleteItem action', function(assert) {
  const actionSpy = sinon.spy(JournalCreators, 'deleteJournalEntry');
  new DataHelper(this.get('redux')).fetchIncidentDetails();

  return init.then(() => {
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    this.$('header .delete button').click();
    return wait().then(() => {
      assert.equal($('#modalDestination .respond-confirmation-dialog').length, 1, 'The confirmation dialog is showing');
      $('.modal-footer-buttons .is-danger button').click();
      return wait();
    }).then(() => {
      assert.ok(actionSpy.calledOnce, 'The deleteJournalEntry action was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('Updates to the description dispatches an updateJournalEntry action', function(assert) {
  const actionSpy = sinon.spy(JournalCreators, 'updateJournalEntry');
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  return init.then(() => {
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    return editableFieldHelper.updateEditableField('.rsa-incident-journal-entry__note', 'Assigned to me. Taking a look').then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateJournalEntry action was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('Changes to the milestone dispatches an updateJournalEntry action', function(assert) {
  const actionSpy = sinon.spy(JournalCreators, 'updateJournalEntry');
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  return init.then(() => {
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    clickTrigger('.rsa-incident-journal-entry__milestone');
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 9, 'There are 9 milestone options available');
    selectOption();
    return wait().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateJournalEntry action creators was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('An error flash message is displayed if there is an error updating journal entry', function(assert) {
  assert.expect(2);
  new DataHelper(this.get('redux')).fetchIncidentDetails(); // to ensure no reducer state errors
  return init.then(() => {
    const done = throwSocket();
    patchFlash((flash) => {
      const translation = getOwner(this).lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.updateFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    return editableFieldHelper.updateEditableField('.rsa-incident-journal-entry__note', 'Assigned to me. Taking a look');
  });
});

test('An error flash message is displayed if there is an error deleting the journal entry', function(assert) {
  assert.expect(2);
  new DataHelper(this.get('redux')).fetchIncidentDetails(); // to ensure no reducer state errors
  return init.then(() => {
    const done = throwSocket();
    patchFlash((flash) => {
      const translation = getOwner(this).lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.deleteFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry incidentId='INC-1234' entry=journalEntry}}`);
    this.$('header .delete button').click();
    return wait().then(() => {
      $('.modal-footer-buttons .is-danger button').click();
      return wait();
    });
  });
});