import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import DataHelper from '../../../../../helpers/data-helper';
import { getAllMilestoneTypes } from 'respond/actions/creators/dictionary-creators';
import * as JournalCreators from 'respond/actions/creators/journal-creators';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';
import RSVP from 'rsvp';
import $ from 'jquery';

let dispatchSpy, redux, initialize;

const journalEntry = {
  id: '12',
  author: 'Tony',
  notes: 'Assigned to you. Please take a look.',
  created: 1483990366970,
  milestone: 'CONTAINMENT'
};

moduleForComponent('rsa-incident/journal/entry', 'Integration | Component | Remediation Journal Entry', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');

    // initialize all of the required data into redux app state
    initialize = RSVP.allSettled([
      redux.dispatch(getAllMilestoneTypes())
    ]);
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
  return initialize.then(() => {
    this.set('journalEntry', journalEntry);
    this.render(hbs`{{rsa-incident/journal/entry entry=journalEntry}}`);
    assert.equal(this.$('.rsa-incident-journal-entry__milestone').text().trim(), 'Milestone: Containment', 'The milestone is displayed as expected');
    assert.equal(this.$('.rsa-incident-journal-entry__note .editable-field__value').text().trim(), 'Assigned to you. Please take a look.', 'The description is displayed as expected');
  });
});

test('The delete button dispatches a deleteItem action', function(assert) {
  const actionSpy = sinon.spy(JournalCreators, 'deleteJournalEntry');
  new DataHelper(this.get('redux')).fetchIncidentDetails();

  return initialize.then(() => {
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
