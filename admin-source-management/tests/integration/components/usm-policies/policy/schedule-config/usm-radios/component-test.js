import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy/schedule-config/usm-radios', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(removeFromSelectedSettingsSpy = sinon.spy(policyWizardCreators, 'removeFromSelectedSettings'));
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.reset());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render scan type options when scanType id is passed', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-radios classNames='scanType' selectedSettingId='scanType'}}`);
    assert.equal(findAll('.scanType').length, 1, 'expected to have scanType component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('should render the agentMode options when agentMode id is passed', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-radios classNames='agentMode' selectedSettingId='agentMode'}}`);
    assert.equal(findAll('.agentMode').length, 1, 'expected to have agentMode component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('It triggers the update policy action creator when the radio button is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-radios classNames='agentMode' selectedSettingId='agentMode'}}`);
    const radioBtn = document.querySelector('.agentMode .rsa-form-radio-label:nth-of-type(2) input');
    await click(radioBtn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-radios classNames='agentMode' selectedSettingId='agentMode'}}`);
    const minusIcon = document.querySelector('.agentMode span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
