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

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/cpu-max', function(hooks) {
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

  test('should render the cpu max component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/cpu-max}}`);
    assert.equal(findAll('.cpu-max').length, 1, 'expected to have root element in DOM');
  });

  test('for a default policy, appropriate class is set for the remove-circle icon', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/cpu-max isDefaultPolicy=true}}`);
    assert.equal(findAll('.title .is-greyed-out').length, 1, 'expected to have remove-circle icon greyed out for a default policy');
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/cpu-max isDefaultPolicy=false}}`);
    assert.equal(findAll('.title .not-greyed-out').length, 1, 'expected to have remove-circle icon enabled for a non-default policy');
  });

  test('should trigger the updatePolicyProperty ac on slider change', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/cpu-max}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when the date stays the same');
    const sliderDiv = document.querySelector('.cpu-max .noUi-tooltip');
    // change the value of the slider
    sliderDiv.textContent = 55;
    await click(sliderDiv);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called when the slider value is changed');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/cpu-max}}`);
    const minusIcon = document.querySelector('.cpu-max span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
