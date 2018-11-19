import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios', function(hooks) {
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

  test('should render Windows Log Collection (enabled) options when enabled id is passed', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios classNames='enabled' selectedSettingId='enabled'}}`);
    assert.equal(findAll('.enabled').length, 1, 'expected to have enabled component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('should render the sendTestLog options when sendTestLog id is passed', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios classNames='sendTestLog' selectedSettingId='sendTestLog'}}`);
    assert.equal(findAll('.sendTestLog').length, 1, 'expected to have sendTestLog component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  // works locally but is flaky on Jenkins
  skip('It triggers the update policy action creator when the radio button is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios classNames='sendTestLog' selectedSettingId='sendTestLog'}}`);
    const radioBtn = document.querySelector('.sendTestLog .rsa-form-radio-wrapper:nth-of-type(2) input');
    await click(radioBtn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  // works locally but is flaky on Jenkins
  skip('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-radios classNames='sendTestLog' selectedSettingId='sendTestLog'}}`);
    const minusIcon = document.querySelector('.sendTestLog span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
