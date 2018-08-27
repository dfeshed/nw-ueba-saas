import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, fillIn, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

import policyCreators from 'admin-source-management/actions/creators/policy-creators';
import sinon from 'sinon';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;


module('Integration | Component | usm-policies/policy/schedule-config/effective-date', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    removeFromSelectedSettingsSpy = sinon.spy(policyCreators, 'removeFromSelectedSettings');
    updatePolicyPropertySpy = sinon.spy(policyCreators, 'updatePolicyProperty');
  });

  hooks.afterEach(function() {
    removeFromSelectedSettingsSpy.reset();
    updatePolicyPropertySpy.reset();
  });

  hooks.after(function() {
    removeFromSelectedSettingsSpy.restore();
    updatePolicyPropertySpy.restore();
  });

  test('should render the effective date component', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/effective-date}}`);
    assert.equal(findAll('.effective-date').length, 1, 'expected to have root element in DOM');
  });

  test('should trigger the updatePolicyProperty action creator on date change', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/effective-date}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when the date stays the same');
    const inputEl = document.querySelector('.date-time input');
    await fillIn(inputEl, '2020');
    await click('.effective-date .datetime-picker-icon');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called on the date change');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/effective-date}}`);
    const minusIcon = document.querySelector('.effective-date span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
