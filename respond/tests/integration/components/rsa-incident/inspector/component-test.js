import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';
import sinon from 'sinon';
import UIStateActions from 'respond/actions/creators/incidents-creators';

let dispatchSpy;

moduleForComponent('rsa-incident-inspector', 'Integration | Component | Incident Inspector', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.inject.service('redux');
    const redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.reset();
  }
});

// @workaround We skip this integration test for now because there is an issue in ember-engines with integration
// tests for components that use a `{{link-to}}`.  The fix is in ember-engines 0.5.0-beta2 so we should be able to
// stop skipping this test once we upgrade.
// @see https://github.com/ember-engines/ember-engines/issues/294 regarding the issue and
// https://github.com/ember-engines/ember-engines/pull/295 for the PR that fixes it
skip('it renders - to test, please upgrade to ember-engines 0.5.0-beta2+', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/inspector}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-inspector');
    assert.equal($el.length, 1, 'Expected to find inspector root element in DOM.');

    [ '.name', '.risk-score span' ].forEach((selector) => {
      const $field = $el.find(selector);
      assert.ok($field.text().trim(), `Expected to find non-empty field element in DOM for: ${selector}`);
    });
  });
});

skip('clicking the view modes sends action - to test, please upgrade to ember-eengines 0.5.0-beta2+', function(assert) {
  const actionSpy = sinon.spy(UIStateActions, 'setViewMode');
  this.render(hbs`{{rsa-incident/journal}}`);
  this.$().find('.js-test-view-mode').click();
  assert.ok(dispatchSpy.calledOnce);
  assert.ok(actionSpy.calledOnce);
  actionSpy.reset();

});