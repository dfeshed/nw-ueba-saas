import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click, triggerEvent } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/custom-config', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(removeFromSelectedSettingsSpy = sinon.spy(policyWizardCreators, 'removeFromSelectedSettings'));
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render customConfig component when id is customConfig', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId='customConfig'}}`);
    assert.equal(findAll('.custom-config').length, 1, 'expected to have customConfig root input element in DOM');
  });

  test('It triggers the update policy action creator when the custom setting value is changed', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId='customConfig'}}`);
    const inputEl = document.querySelector('.custom-config__textarea');
    inputEl.value = 'foobar';
    await triggerEvent(inputEl, 'blur');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId='customConfig'}}`);
    const minusIcon = document.querySelector('.custom-config span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });

  test('It shows the error message when the custom setting is invalid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    let customSettingValue = '';
    const expectedMessage = translation.t('adminUsm.policyWizard.edrPolicy.customConfigInvalidMsg');
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId='customConfig'}}`);
    const inputEl = document.querySelector('.custom-config__textarea');
    inputEl.value = customSettingValue;
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.is-error').length, 1, 'Error is showing');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing for blank value: ${expectedMessage}`);

    // valid input
    customSettingValue = '"trackingConfig": {"uniqueFilterSeconds": 28800,"beaconStdDev": 2.0}';
    inputEl.value = customSettingValue;
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.input-error')[0].innerText, '', 'No error message when valid input');

    // invalid input - greater than 4k
    let testSetting = '';
    for (let index = 0; index < 110; index++) {
      testSetting += 'the-description-is-greater-than-4000-';
    }
    inputEl.value = testSetting;
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing for too long: ${expectedMessage}`);
  });
});