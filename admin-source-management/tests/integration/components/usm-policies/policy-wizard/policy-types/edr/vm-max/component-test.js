import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState, removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/vm-max', function(hooks) {
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
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render the virtual machine max component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMaxVm(85)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/vm-max}}`);
    assert.equal(findAll('.vm-max').length, 1, 'expected to have root element in DOM');
  });

  test('should trigger the updatePolicyProperty ac on slider change', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMaxVm(85)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/vm-max}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when the date stays the same');
    const sliderDiv = document.querySelector('.vm-max .noUi-tooltip');
    // change the value of the slider
    sliderDiv.textContent = 55;
    await click(sliderDiv);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called when the slider value is changed');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMaxVm(75)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/vm-max}}`);
    const minusIcon = document.querySelector('.vm-max span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
