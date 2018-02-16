import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import DataHelper from '../../../../../helpers/data-helper';
import { getAllMilestoneTypes } from 'respond/actions/creators/dictionary-creators';
import * as JournalCreators from 'respond/actions/creators/journal-creators';
import sinon from 'sinon';
import RSVP from 'rsvp';
import wait from 'ember-test-helpers/wait';
import { throwSocket } from '../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { getOwner } from '@ember/application';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let dispatchSpy, redux, init;


moduleForComponent('rsa-incident/journal/new-entry', 'Integration | Component | Journal New Entry', {
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

test('The rsa-incident/journal/new-entry component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-incident/journal/new-entry}}`);
  assert.equal(this.$('.rsa-incident-journal__new-entry').length, 1, 'The rsa-incident/journal/new-entry component should be found in the DOM');
  assert.equal(this.$('.save-journal.is-disabled').length, 1, 'The save journal button is disabled by default (when there is no text)');
});

test('The createJournalEntry action is dispatched on save button click', function(assert) {
  const actionSpy = sinon.spy(JournalCreators, 'createJournalEntry');
  new DataHelper(this.get('redux')).fetchIncidentDetails(); // to ensure no reducer state errors

  return init.then(() => {
    this.render(hbs`{{rsa-incident/journal/new-entry incidentId='INC-1234' notes='Example text'}}`);
    assert.equal(this.$('.save-journal.is-disabled').length, 0, 'The save journal button is not disabled (when there is text)');
    this.$('.save-journal button').click();
    return wait().then(() => {
      assert.ok(actionSpy.calledOnce, 'The createJournalEntry action was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('An error flash message is displayed if there is an error saving the new journal entry', function(assert) {
  assert.expect(2);
  new DataHelper(this.get('redux')).fetchIncidentDetails(); // to ensure no reducer state errors
  return init.then(() => {
    const done = throwSocket();
    patchFlash((flash) => {
      const translation = getOwner(this).lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.createFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.render(hbs`{{rsa-incident/journal/new-entry incidentId='INC-1234' notes='Example text'}}`);
    this.$('.save-journal button').click();
  });
});