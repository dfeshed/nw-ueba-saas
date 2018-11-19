import { module, test } from 'qunit';
import { click, findAll, settled, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { setupRenderingTest } from 'ember-qunit';
import DataHelper from '../../../../../helpers/data-helper';
import { getAllMilestoneTypes } from 'respond/actions/creators/dictionary-creators';
import * as JournalCreators from 'respond/actions/creators/journal-creators';
import sinon from 'sinon';
import RSVP from 'rsvp';
import { throwSocket } from '../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let dispatchSpy, redux, setup;

module('Integration | Component | Journal New Entry', function(hooks) {
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

  test('The rsa-incident/journal/new-entry component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-incident/journal/new-entry}}`);
    assert.equal(this.$('.rsa-incident-journal__new-entry').length, 1, 'The rsa-incident/journal/new-entry component should be found in the DOM');
    assert.equal(this.$('.save-journal.is-disabled').length, 1, 'The save journal button is disabled by default (when there is no text)');
  });

  test('The createJournalEntry action is dispatched on save button click', async function(assert) {
    assert.expect(4);
    const done = assert.async();

    const actionSpy = sinon.spy(JournalCreators, 'createJournalEntry');
    new DataHelper(redux).fetchIncidentDetails(); // to ensure no reducer state errors

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.updateSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    await setup();
    await render(hbs`{{rsa-incident/journal/new-entry incidentId='INC-1234' notes='Example text'}}`);
    assert.equal(findAll('.save-journal.is-disabled').length, 0, 'The save journal button is not disabled (when there is text)');

    await click('.save-journal button');
    await settled();

    assert.ok(actionSpy.calledOnce, 'The createJournalEntry action was called once');
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('An error flash message is displayed if there is an error saving the new journal entry', async function(assert) {
    assert.expect(3);

    new DataHelper(redux).fetchIncidentDetails(); // to ensure no reducer state errors

    await setup();

    const done = throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.entities.actionMessages.createFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    await render(hbs`{{rsa-incident/journal/new-entry incidentId='INC-1234' notes='Example text'}}`);
    assert.equal(findAll('.save-journal.is-disabled').length, 0, 'The save journal button is not disabled (when there is text)');

    await click('.save-journal button');
    await settled();
  });
});
