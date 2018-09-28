import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState, removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy/schedule-config/primary-address', function(hooks) {
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
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.reset());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render primaryAddress component when id is primaryAddress', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/primary-address selectedSettingId='primaryAddress'}}`);
    assert.equal(findAll('.primary-address').length, 1, 'expected to have primaryAddress root input element in DOM');
  });

  test('It triggers the update policy action creator when the endpoint server value is changed', async function(assert) {
    new ReduxDataHelper(setState)
        .policyWiz()
        .policyWizEndpointServers()
        .build();
    // const state = this.owner.lookup('service:redux').getState();
    // this.set('endpointsList', state.usm.policyWizard.listOfEndpointServers);
    await render(hbs`{{usm-policies/policy/schedule-config/primary-address selectedSettingId='primaryAddress'}}`);
    await selectChoose('.primary-address__list', '.ember-power-select-option', 0);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/primary-address selectedSettingId='primaryAddress'}}`);
    const minusIcon = document.querySelector('.primary-address span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});