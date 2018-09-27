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

  test('should render httpPort component when id is httpPort', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='httpPort'}}`);
    assert.equal(findAll('.httpPort').length, 1, 'expected to have httpPort root input element in DOM');
  });

  test('should render udpPort component when id is udpPort', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='udpPort'}}`);
    assert.equal(findAll('.udpPort').length, 1, 'expected to have udpPort root input element in DOM');
  });

  test('It triggers the update policy action creator when the port value is changed', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='httpPort'}}`);
    const inputEl = document.querySelector('.httpPort input');
    await fillIn(inputEl, '2020');
    await triggerEvent(inputEl, 'blur');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='httpPort'}}`);
    const minusIcon = document.querySelector('.httpPort span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });

  test('It triggers the error message when the port is invalid', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/usm-ports selectedSettingId='httpPort'}}`);
    const inputEl = document.querySelector('.httpPort input');
    await fillIn(inputEl, '55');
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.port-error').length, 0, 'no error message when port is valid');
    await fillIn(inputEl, '-1');
    await triggerEvent(inputEl, 'blur');
    assert.equal(findAll('.port-error').length, 1, 'expected to have error message when port is invalid');

  });
});
