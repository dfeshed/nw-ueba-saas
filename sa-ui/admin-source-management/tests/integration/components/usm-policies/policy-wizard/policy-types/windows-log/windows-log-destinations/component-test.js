import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, findAll, click, triggerEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../../../helpers/redux-async-helpers';
import { selectedPrimaryLogServer } from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';

let redux, setState;

module('Integration | Component | usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
      redux = this.owner.lookup('service:redux');
    };
  });

  test('should render primaryDestination component when id is primaryDestination', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations selectedSettingId='primaryDestination'}}`);
    assert.equal(findAll('.primaryDestination').length, 1, 'expected to have primaryDestination root input element in DOM');
  });

  test('should render secondaryDestination component when id is secondaryDestination', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations selectedSettingId='secondaryDestination'}}`);
    assert.equal(findAll('.secondaryDestination').length, 1, 'expected to have secondaryDestination root input element in DOM');
  });

  test('Some primary destination options can be disabled for a filePolicy', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizWinLogLogServers()
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations selectedSettingId='primaryDestination'}}`);
    await click('.ember-power-select-placeholder');
    assert.equal(findAll('.ember-power-select-option[aria-disabled=true]').length, 3, 'Primary Log servers with version older than 11.4 are disabled for a file policy');
    // disabled destination options should have a tooltip to show why they are disabled
    const expectedDisabledTooltip = 'This Log Decoder / Log Collector needs to be on version 11.4 or above to receive file logs from the agent.';
    const [disabledDest0] = findAll('.ember-power-select-option[aria-disabled=true] .tooltip-text');
    await triggerEvent(disabledDest0, 'mouseover');
    const actualDisabledTooltip = findAll('.tool-tip-value')[0].innerText.trim();
    assert.equal(actualDisabledTooltip, expectedDisabledTooltip, 'disabled destination option tooltip is as expected');
  });

  test('Some secondary destination options can be disabled for a filePolicy', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizWinLogLogServers()
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations selectedSettingId='secondaryDestination'}}`);
    await click('.ember-power-select-placeholder');
    assert.equal(findAll('.ember-power-select-option[aria-disabled=true]').length, 3, 'Secondary Log servers with version older than 11.4 are disabled for a file policy');
    // disabled destination options should have a tooltip to show why they are disabled
    const expectedDisabledTooltip = 'This Log Decoder / Log Collector needs to be on version 11.4 or above to receive file logs from the agent.';
    const [disabledDest0] = findAll('.ember-power-select-option[aria-disabled=true] .tooltip-text');
    await triggerEvent(disabledDest0, 'mouseover');
    const actualDisabledTooltip = findAll('.tool-tip-value')[0].innerText.trim();
    assert.equal(actualDisabledTooltip, expectedDisabledTooltip, 'disabled destination option tooltip is as expected');
  });

  test('It triggers the update policy action creator when the log server value is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogLogServers()
      .build();
    const selectedSettingId = 'primaryDestination';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations selectedSettingId=selectedSettingId}}`);
    const expectedValue = '10.10.10.10';
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await selectChoose('.windows-log-destinations__list', '.ember-power-select-option', 0);
    await onChange;
    const actualValue = selectedPrimaryLogServer(redux.getState());
    assert.equal(actualValue.host, expectedValue, `${selectedSettingId} updated to ${actualValue.host}`);
  });

  test('It shows the error message when the primaryDestination is invalid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const visitedExpected = ['policy.primaryDestination'];
    const expectedMessage = translation.t('adminUsm.policyWizard.windowsLogPolicy.windowsLogDestinationInvalidMsg');
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogLogServers()
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations selectedSettingId='primaryDestination'}}`);
    assert.equal(findAll('.windows-log-destinations__list .selector-error').length, 1, 'Error is showing');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);

    // valid input
    await selectChoose('.windows-log-destinations__list', '.ember-power-select-option', 0);
    assert.equal(findAll('.windows-log-destinations__list .selector-error').length, 0, 'Error is not showing for valid input');
    assert.equal(findAll('.input-error')[0].innerText, '', 'No error message when valid input');
  });
});