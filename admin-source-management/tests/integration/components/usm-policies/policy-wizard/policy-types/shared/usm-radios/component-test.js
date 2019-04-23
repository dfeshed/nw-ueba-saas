import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../../../helpers/redux-async-helpers';
import {
  // radioButtonOption,
  radioButtonValue
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

let redux, setState;

module('Integration | Component | usm-policies/policy-wizard/policy-types/shared/usm-radios', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
      redux = this.owner.lookup('service:redux');
    };
  });

  // ====================================================================
  // test usage for some edr settings
  // ====================================================================

  test('should render edrPolicy scan type options when scanType id is passed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId='scanType'}}`);
    assert.equal(findAll('.scanType').length, 1, 'expected to have scanType component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('should render edrPolicy agentMode options when agentMode id is passed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId='agentMode'}}`);
    assert.equal(findAll('.agentMode').length, 1, 'expected to have agentMode component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('It triggers the update policy action creator when the edrPolicy scanType radio selection is changed', async function(assert) {
    const selectedSettingId = 'scanType';
    this.set('selectedSettingId', selectedSettingId);
    const initValue = 'DISABLED';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizScanType(initValue)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId=selectedSettingId}}`);
    const radioBtn = document.querySelector(`.${selectedSettingId} .rsa-form-radio-wrapper:nth-of-type(2) input`);
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await click(radioBtn);
    await onChange;
    const actualValue = radioButtonValue(redux.getState(), selectedSettingId);
    const expectedValue = 'ENABLED';
    assert.equal(actualValue, expectedValue, `${selectedSettingId} updated from ${initValue} to ${actualValue}`);
  });

  test('It triggers the update policy action creator when the edrPolicy agentMode radio selection is changed', async function(assert) {
    const selectedSettingId = 'agentMode';
    this.set('selectedSettingId', selectedSettingId);
    const initValue = 'INSIGHTS';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizAgentMode(initValue)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId=selectedSettingId}}`);
    const radioBtn = document.querySelector(`.${selectedSettingId} .rsa-form-radio-wrapper:nth-of-type(2) input`);
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await click(radioBtn);
    await onChange;
    const actualValue = radioButtonValue(redux.getState(), selectedSettingId);
    const expectedValue = 'ADVANCED';
    assert.equal(actualValue, expectedValue, `${selectedSettingId} updated from ${initValue} to ${actualValue}`);
  });

  // ====================================================================
  // test usage for some windowsLog settings
  // ====================================================================

  test('should render windowsLogPolicy enabled options when enabled id is passed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId='enabled'}}`);
    assert.equal(findAll('.enabled').length, 1, 'expected to have enabled component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('should render windowsLogPolicy sendTestLog options when sendTestLog id is passed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId='sendTestLog'}}`);
    assert.equal(findAll('.sendTestLog').length, 1, 'expected to have sendTestLog component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('It triggers the update policy action creator when the windowsLogPolicy enabled radio selection is changed', async function(assert) {
    const selectedSettingId = 'enabled';
    this.set('selectedSettingId', selectedSettingId);
    const initValue = false;
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizScanType(initValue)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId=selectedSettingId}}`);
    const radioBtn = document.querySelector(`.${selectedSettingId} .rsa-form-radio-wrapper:nth-of-type(2) input`);
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await click(radioBtn);
    await onChange;
    const actualValue = radioButtonValue(redux.getState(), selectedSettingId);
    const expectedValue = true;
    assert.equal(actualValue, expectedValue, `${selectedSettingId} updated from ${initValue} to ${actualValue}`);
  });

  test('It triggers the update policy action creator when the windowsLogPolicy sendTestLog radio selection is changed', async function(assert) {
    const selectedSettingId = 'sendTestLog';
    this.set('selectedSettingId', selectedSettingId);
    const initValue = false;
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizScanType(initValue)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios selectedSettingId=selectedSettingId}}`);
    const radioBtn = document.querySelector(`.${selectedSettingId} .rsa-form-radio-wrapper:nth-of-type(2) input`);
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await click(radioBtn);
    await onChange;
    const actualValue = radioButtonValue(redux.getState(), selectedSettingId);
    const expectedValue = true;
    assert.equal(actualValue, expectedValue, `${selectedSettingId} updated from ${initValue} to ${actualValue}`);
  });

});
