import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';
import sinon from 'sinon';
import UIStateActions from 'respond/actions/creators/incidents-creators';

let dispatchSpy;

moduleForComponent('rsa-incident-toolbar', 'Integration | Component | Incident Toolbar', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.inject.service('redux');
    const redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-incident/toolbar}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-toolbar');
    assert.equal($el.length, 1, 'Expected to find toolbar root element in DOM.');

    const $journal = $el.find('.js-test-journal');
    assert.equal($journal.length, 1, 'Expected to find Journal toggle element in DOM.');
  });
});

test('clicking Journal button triggers action', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/toolbar}}`);
  return wait().then(() => {
    const actionSpy = sinon.spy(UIStateActions, 'toggleTasksAndJournalPanel');
    this.$('.js-test-journal button').click();
    assert.ok(dispatchSpy.callCount);
    assert.ok(actionSpy.calledOnce);
    actionSpy.restore();
  });
});