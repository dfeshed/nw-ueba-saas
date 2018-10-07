import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, fillIn, triggerEvent, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy/schedule-config/usm-ports', function(hooks) {
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

  test('should render primaryHttpsPort component when id is primaryHttpsPort', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='primaryHttpsPort'}}`);
    assert.equal(findAll('.primaryHttpsPort').length, 1, 'expected to have primaryHttpsPort root input element in DOM');
  });

  test('should render primaryUdpPort component when id is primaryUdpPort', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='primaryUdpPort'}}`);
    assert.equal(findAll('.primaryUdpPort').length, 1, 'expected to have primaryUdpPort root input element in DOM');
  });

  test('It triggers the update policy action creator when the port value is changed', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='primaryHttpsPort'}}`);
    const inputEl = document.querySelector('.primaryHttpsPort input');
    await fillIn(inputEl, '2020');
    await triggerEvent(inputEl, 'blur');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='primaryHttpsPort'}}`);
    const minusIcon = document.querySelector('.primaryHttpsPort span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });

  test('It triggers the error message when the port is invalid', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='primaryHttpsPort'}}`);
    const inputEl = document.querySelector('.primaryHttpsPort input');
    await fillIn(inputEl, '55');
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.port-error').length, 0, 'no error message when port is valid');
    await fillIn(inputEl, '-1');
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.port-error').length, 1, 'expected to have error message when port is invalid');

  });
});
