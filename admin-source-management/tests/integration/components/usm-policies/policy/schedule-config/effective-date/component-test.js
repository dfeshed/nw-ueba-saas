import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, fillIn, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState, removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/effective-date', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(removeFromSelectedSettingsSpy = sinon.spy(policyWizardCreators, 'removeFromSelectedSettings'));
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.reset());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render the effective date component', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/effective-date}}`);
    assert.equal(findAll('.scan-start-date').length, 1, 'expected to have root element in DOM');
  });

  test('should trigger the updatePolicyProperty action creator on date change', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/effective-date}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when the date stays the same');
    const inputEl = document.querySelector('.date-time input');
    await fillIn(inputEl, '2020');
    await click('.scan-start-date .datetime-picker-icon');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called on the date change');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/effective-date}}`);
    const minusIcon = document.querySelector('.scan-start-date span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });

  test('It shows the error message when the scanStartDate is invalid', async function(assert) {
    const i18n = this.owner.lookup('service:i18n');
    const scanStartDate = '';
    const visitedExpected = ['policy.scanStartDate'];
    const expectedMessage = i18n.t('adminUsm.policyWizard.edrPolicy.scanStartDateInvalidMsg');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizScanStartDate(scanStartDate)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/effective-date selectedSettingId='scanStartDate'}}`);
    assert.ok(findAll('.input-error').length, 1, 'Error is showing');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });
});
