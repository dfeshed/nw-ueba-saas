import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';
import sinon from 'sinon';
import UIStateActions from 'respond/actions/creators/incidents-creators';

let dispatchSpy;

moduleForComponent('rsa-incident-journal', 'Integration | Component | Incident Journal', {
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
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/journal}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-journal');
    assert.equal($el.length, 1, 'Expected to find journal root element in DOM.');

    const $entries = $el.find('.rsa-incident-journal-entry');
    assert.ok($entries.length, 'Expected to find at least one data table body row element in DOM.');
  });
});

test('clicking close button triggers action', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/journal}}`);
  return wait().then(() => {
    const actionSpy = sinon.spy(UIStateActions, 'toggleJournalPanel');
    this.$().find('.js-test-journal-close').click();
    assert.ok(dispatchSpy.callCount);
    assert.ok(actionSpy.calledOnce);
    actionSpy.restore();
  });
});