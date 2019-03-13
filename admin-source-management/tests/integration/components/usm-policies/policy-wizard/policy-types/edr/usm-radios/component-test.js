import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/edr-radios', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render scan type options when scanType id is passed', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-radios classNames='scanType' selectedSettingId='scanType'}}`);
    assert.equal(findAll('.scanType').length, 1, 'expected to have scanType component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('should render the agentMode options when agentMode id is passed', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-radios classNames='agentMode' selectedSettingId='agentMode'}}`);
    assert.equal(findAll('.agentMode').length, 1, 'expected to have agentMode component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  // works locally but is flaky on Jenkins
  skip('It triggers the update policy action creator when the radio button is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-radios classNames='agentMode' selectedSettingId='agentMode'}}`);
    const radioBtn = document.querySelector('.agentMode .rsa-form-radio-wrapper:nth-of-type(2) input');
    await click(radioBtn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

});
