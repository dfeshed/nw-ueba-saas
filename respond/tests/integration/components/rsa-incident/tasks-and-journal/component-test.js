import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import DataHelper from '../../../../helpers/data-helper';
import * as IncidentsCreators from 'respond/actions/creators/incidents-creators';
import { findAll, render, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

module('Integration | Component | Remediation Tasks and Journal Panel', function(hooks) {
  let dispatchSpy;
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from({}));
    const redux = this.owner.lookup('service:redux');
    this.set('redux', redux);
    dispatchSpy = sinon.spy(redux, 'dispatch');
  });
  hooks.afterEach(function() {
    dispatchSpy.restore();
  });


  test('The rsa-incident/tasks-and-journal component renders to the DOM', async function(assert) {
    new DataHelper(this.get('redux')).fetchIncidentDetails();
    await render(hbs`{{rsa-incident/tasks-and-journal}}`);
    assert.equal(findAll('.rsa-journal-and-tasks').length, 1, 'The rsa-incident/tasks-and-journal component should be found in the DOM');
    assert.equal(findAll('.rsa-incident-journal-entry').length, 4, 'There are four journal entries in the DOM');
  });

  test('Clicking on the Remediation Tasks tab dispatches the setTasksJournalMode action', async function(assert) {
    const actionSpy = sinon.spy(IncidentsCreators, 'setTasksJournalMode');
    new DataHelper(this.get('redux')).fetchIncidentDetails();
    await render(hbs`{{rsa-incident/tasks-and-journal}}`);
    assert.equal(findAll('.task-list').length, 0, 'The Remediation Task List element is NOT in the DOM');
    await click('.rsa-tab.remediation-tab');
    assert.ok(actionSpy.calledOnce, 'The setTasksJournalMode creator was called once');
    actionSpy.restore();
    assert.equal(findAll('.task-list').length, 1, 'The Remediation Task List element is in the DOM');
    await click('.rsa-tab.journal-tab'); // revert back to initial journal tab active state

  });

  test('Clicking on the close button dispatches a toggleTasksAndJournalPanel action', async function(assert) {
    const actionSpy = sinon.spy(IncidentsCreators, 'toggleTasksAndJournalPanel');
    new DataHelper(this.get('redux')).fetchIncidentDetails();
    await render(hbs`{{rsa-incident/tasks-and-journal}}`);
    await click('.close-panel button');
    assert.ok(actionSpy.calledOnce, 'The toggleTasksAndJournalPanel creator was called once');
    actionSpy.restore();
  });
});