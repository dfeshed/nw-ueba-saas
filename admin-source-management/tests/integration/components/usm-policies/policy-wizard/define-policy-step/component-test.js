import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState, setStateOldSchool;

module('Integration | Component | usm-policies/policy-wizard/define-policy-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    setStateOldSchool = (state) => {
      const fullState = { usm: { policyWizard: state } };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  // hooks.afterEach(function() {
  // });

  // hooks.after(function() {
  // });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.define-policy-step').length, 1, 'The component appears in the DOM');
  });

  test('All the components in the available settings is rendered on the UI', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .available-setting').length, 16, 'All available settings rendered on the UI');
  });

  test('All the components in the selected settings is rendered on the UI ', async function(assert) {
    const newSelectedSettings = [
      { index: 0, id: 'scanType', label: 'Run Scheduled Scan', isEnabled: false, isGreyedOut: false, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios' },
      { index: 1, id: 'scanStartDate', label: 'Effective Date', isEnabled: false, isGreyedOut: true, component: 'usm-policies/policy-wizard/policy-types/edr/effective-date' }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz().build().usm.policyWizard;
    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 2, 'All selected settings rendered on the UI');
  });

  test('When policy is Windows Log policy, All the components in the available settings is rendered on the UI', async function(assert) {
    new ReduxDataHelper(setState).policyWiz('windowsLogPolicy').build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .available-setting').length, 6, 'All windows log policy available settings rendered on the UI');
  });

  test('When policy is Windows Log policy, All the components in the selected settings is rendered on the UI ', async function(assert) {
    const newSelectedSettings = [
      { index: 3, id: 'primaryDestination', label: 'adminUsm.policyWizard.windowsLogPolicy.primaryDestination', isEnabled: false, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/windows-log/windows-log-destinations', defaults: [{ field: 'primaryDestination', value: '' }]
      }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz('windowsLogPolicy').build().usm.policyWizard;
    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 1, 'All selected settings rendered on the UI');
  });

  test('Labels, sub-headers and components rendered correctly in available settings', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .heading').length, 6, 'All heading labels rendered correctly');
    assert.equal(findAll('.available-settings .title').length, 16, 'All components in available-settings rendered correctly');
    assert.equal(findAll('.available-settings .rsa-icon-add-circle-1-lined').length, 16, 'The plus icon next to the components is rendered correctly');
  });

  test('Effective date component should be greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .scanStartDate')[0].classList.contains('is-greyed-out'), true, 'Effective date component should be greyed out by default');
  });

  test('Scan frequency component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .recurrenceInterval')[0].classList.contains('is-greyed-out'), true, 'Scan frequency component should be greyed out by default');
  });

  test('Start Time component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .scanStartTime')[0].classList.contains('is-greyed-out'), true, 'Start Time component should be greyed out by default');
  });

  test('CPU Maximum component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .cpuMax')[0].classList.contains('is-greyed-out'), true, 'CPU Maximum component should be greyed out by default');
  });

  test('Virtual Machine Maximum component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .cpuMaxVm')[0].classList.contains('is-greyed-out'), true, 'Virtual Machine Maximum component should be greyed out by default');
  });

  test('All the components driven by usm-radios component is shown in the available settings', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    // assert.equal(findAll('.available-settings .captureFloatingCode').length, 1, 'Capture Floating code component is shown in the available settings');
    assert.equal(findAll('.available-settings .scanMbr').length, 1, 'Download Master Boot Record component is shown in the available settings');
    // assert.equal(findAll('.available-settings .filterSignedHooks').length, 1, 'Signed Modules component is shown in the available settings');
    assert.equal(findAll('.available-settings .requestScanOnRegistration').length, 1, 'New System added component is shown in the available settings');
    assert.equal(findAll('.available-settings .blockingEnabled').length, 1, 'Blocking Action component is shown in the available settings');
    assert.equal(findAll('.available-settings .agentMode').length, 1, 'Monitoring Mode component is shown in the available settings');
  });

  test('All Endpoint Server components are shown in the available settings', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .primaryAddress').length, 1, 'Primary Address component is shown in the available settings');
    assert.equal(findAll('.available-settings .primaryHttpsPort').length, 1, 'Primary HTTPS Port component is shown in the available settings');
    assert.equal(findAll('.available-settings .primaryHttpsBeaconInterval').length, 1, 'Primary HTTPS Beacon Interval component is shown in the available settings');
    assert.equal(findAll('.available-settings .primaryUdpPort').length, 1, 'Primary UDP Port component is shown in the available settings');
    assert.equal(findAll('.available-settings .primaryUdpBeaconInterval').length, 1, 'Primary UDP Beacon Interval component is shown in the available settings');
  });

  test('No available settings should be rendered when isEnabled flag is false', async function(assert) {
    const newAvailableSettings = [
      { index: 0, id: 'scanType', label: 'Run Scheduled Scan', isEnabled: false, isGreyedOut: false, component: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'scanStartDate', label: 'Effective Date', isEnabled: false, isGreyedOut: true, component: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz().build().usm.policyWizard;
    setStateOldSchool({ ...initialState, availableSettings: newAvailableSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .available-setting').length, 0, 'No available settings should be rendered when isEnabled flag is false');
  });

  test('No other selected settings should be rendered when scanScheduleId is not in the selected settings ', async function(assert) {
    const newSelectedSettings = [
      { index: 0, id: 'scanType', label: 'Run Scheduled Scan', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy-wizard/policy-types/shared/usm-radios' },
      { index: 1, id: 'scanStartDate', label: 'Effective Date', isEnabled: true, isGreyedOut: false, parentId: 'scanType', component: 'usm-policies/policy-wizard/policy-types/edr/effective-date' }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz().build().usm.policyWizard;
    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 2, 'All selected settings rendered on the UI');
    // simulate a click on the minus icon next to scan schedule.
    // this triggers RESET_SCAN_SCHEDULE_TO_DEFAULTS reducer and clears out all selected settings
    const minusIcon = document.querySelector('.scanType-setting span .rsa-icon-remove-circle-1-lined');
    await click(minusIcon);
    assert.equal(findAll('.selected-settings .selected-setting').length, 0, 'No other selected settings are rendered when scanSchedule is not rendered');
  });

  test('Scan Schedule label should be present if scanScheduleId is added the selected settings', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    // simulate a click on the plus icon next to scan schedule.
    // this triggers 2 reducers ACTION_TYPES.ADD_TO_SELECTED_SETTINGS and ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS
    const plusIcon = document.querySelector('.scanType span .rsa-icon');
    await click(plusIcon);
    assert.equal(findAll('.selected-settings .heading').length, 1, 'Scan schedule main label is added to the selectedSettings');
    assert.equal(findAll('.selected-settings .selected-setting').length, 1, 'All components in selected-settings rendered correctly');
    assert.equal(findAll('.selected-settings .rsa-icon-remove-circle-1-lined').length, 1, 'The minus icon next to the components in selected-settings is rendered correctly');
  });

  test('When policy is Windows Log policy, primary destination is shown in the available settings', async function(assert) {
    new ReduxDataHelper(setState).policyWiz('windowsLogPolicy').build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .primaryDestination').length, 1, 'Primary Destination component is shown in the available settings');
  });

  test('When policy is Windows Log policy, Windows log channel filters is shown in the available settings', async function(assert) {
    const channelFilters = [ { channel: 'System', filterType: 'INCLUDE', eventId: 'ALL' } ];
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .channelFilters').length, 1, 'Channel Filters component is shown in the available settings');
  });
});
