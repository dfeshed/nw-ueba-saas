import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, fillIn, triggerEvent, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState, updatePolicyPropertySpy;
const spys = [];
const channelFilters = [ { channel: 'System', filterType: 'INCLUDE', eventId: 'ALL' } ];

module('Integration | Component | usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render the root channelFilters component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    assert.equal(findAll('.channel-filter-container').length, 1, 'expected to have channelFilters root input element in DOM');
  });

  test('It triggers the update policy action creator when the channel name is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    await selectChoose('.windows-log-channel-name', '.ember-power-select-option', 3);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the channel filter is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    await selectChoose('.windows-log-channel-filter', '.ember-power-select-option', 1);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the event id is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    const value = 20;
    const [eventIdEl] = findAll('.windows-log-channel-list .event-id input');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when a new row is added', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    const addRow = document.querySelector('.windows-log-channel-filters .add-row .add-channel-button');
    await click(addRow);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when a delete row is clicked', async function(assert) {
    const newFilters = [ { channel: 'System', filterType: 'INCLUDE', eventId: 'ALL' }, { channel: 'Security', filterType: 'INCLUDE', eventId: 'ALL' }];
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    const [, deleteBtn] = document.querySelectorAll('.windows-log-channel-filters .delete-button');
    await click(deleteBtn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It shows correct error message when the channel name is blank', async function(assert) {
    const newFilters = [ { channel: '', filterType: 'INCLUDE', eventId: 'ALL' }];
    const translation = this.owner.lookup('service:i18n');
    const visitedExpected = ['policy.channelFilters'];
    const expectedMessage = translation.t('adminUsm.policyWizard.windowsLogPolicy.invalidChannelFilter');
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    // await pauseTest();
    assert.equal(findAll('.windows-log-channel-name .selector-error').length, 1, 'Error is showing');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);

    // valid input
    await selectChoose('.windows-log-channel-name', '.ember-power-select-option', 0);
    assert.equal(findAll('.windows-log-channel-name .selector-error').length, 0, 'Error is not showing for valid input');
    assert.equal(findAll('.input-error')[0].innerText, '', 'No error message when valid input');
  });

  test('It shows correct error message when the event id is invalid', async function(assert) {
    const newFilters = [ { channel: 'System', filterType: 'INCLUDE', eventId: 'foo$' }];
    const translation = this.owner.lookup('service:i18n');
    const visitedExpected = ['policy.channelFilters'];
    const expectedMessage = translation.t('adminUsm.policyWizard.windowsLogPolicy.invalidEventId');
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters}}`);
    assert.equal(findAll('.event-id .input-error').length, 1, 'Error is showing');
    assert.equal(findAll('.event-id .input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);

    // valid input
    const value = 20;
    const [eventIdEl] = findAll('.windows-log-channel-list .event-id input');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(findAll('.event-id .input-error').length, 0, 'Error is not showing for valid input');
  });
});