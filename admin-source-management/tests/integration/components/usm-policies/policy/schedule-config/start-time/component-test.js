import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { setFlatpickrDate } from 'ember-flatpickr/test-support/helpers';

import policyCreators from 'admin-source-management/actions/creators/policy-creators';
import sinon from 'sinon';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;


module('Integration | Component | usm-policies/policy/schedule-config/start-time', function(hooks) {
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

  test('should render the start-time component', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/start-time}}`);
    assert.equal(findAll('.start-time').length, 1, 'expected to have root element in DOM');
  });

  test('should trigger the updatePolicyProperty action creator on time change', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/start-time}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when the time stays the same');
    const inputEl = document.querySelector('.schedule-time input');
    setFlatpickrDate(inputEl, '123456789');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called on the time change');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/start-time}}`);
    const minusIcon = document.querySelector('.start-time span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
