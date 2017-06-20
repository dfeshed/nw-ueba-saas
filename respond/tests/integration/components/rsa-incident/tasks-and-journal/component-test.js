import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import sinon from 'sinon';
import DataHelper from '../../../../helpers/data-helper';
import * as IncidentsCreators from 'respond/actions/creators/incidents-creators';
import wait from 'ember-test-helpers/wait';

let dispatchSpy, redux;

moduleForComponent('rsa-incident/tasks-and-journal', 'Integration | Component | Remediation Tasks and Journal Panel', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The rsa-incident/tasks-and-journal component renders to the DOM', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/tasks-and-journal}}`);
  assert.equal(this.$('.rsa-journal-and-tasks').length, 1, 'The rsa-incident/tasks-and-journal component should be found in the DOM');
  assert.equal(this.$('.rsa-incident-journal-entry').length, 4, 'There are four journal entries in the DOM');
});

test('Clicking on the Remediation Tasks tab dispatches the setTasksJournalMode action', function(assert) {
  const actionSpy = sinon.spy(IncidentsCreators, 'setTasksJournalMode');
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/tasks-and-journal}}`);
  assert.equal(this.$('.task-list').length, 0, 'The Remediation Task List element is NOT in the DOM');
  this.$('.rsa-tab.remediation-tab').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The setTasksJournalMode creator was called once');
    actionSpy.restore();
    assert.equal(this.$('.task-list').length, 1, 'The Remediation Task List element is in the DOM');
    this.$('.rsa-tab.journal-tab').click(); // revert back to initial journal tab active state
  });
});

test('Clicking on the close button dispatches a toggleTasksAndJournalPanel action', function(assert) {
  const actionSpy = sinon.spy(IncidentsCreators, 'toggleTasksAndJournalPanel');
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/tasks-and-journal}}`);
  this.$('.close-panel button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The toggleTasksAndJournalPanel creator was called once');
    actionSpy.restore();
  });
});
