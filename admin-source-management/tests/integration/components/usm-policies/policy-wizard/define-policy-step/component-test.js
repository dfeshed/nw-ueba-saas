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
    assert.equal(findAll('.available-settings .available-setting').length, 6, 'All available settings rendered on the UI');
  });

  test('All the components in the selected settings is rendered on the UI ', async function(assert) {
    const newSelectedSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: false, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz().build().usm.policyWizard;
    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 2, 'All selected settings rendered on the UI');
  });

  test('Effective date component should be greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .effectiveDate')[0].classList.contains('is-greyed-out'), true, 'Effective date component should be greyed out by default');
  });

  test('Scan frequency component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .recurrenceInterval')[0].classList.contains('is-greyed-out'), true, 'Scan frequency component should be greyed out by default');
  });

  test('Start Time component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .startTime')[0].classList.contains('is-greyed-out'), true, 'Start Time component should be greyed out by default');
  });

  test('CPU Maximum component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .cpuMax')[0].classList.contains('is-greyed-out'), true, 'CPU Maximum component should be greyed out by default');
  });

  test('Virtual Machine Maximum component greyed out by default', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .vmMax')[0].classList.contains('is-greyed-out'), true, 'Virtual Machine Maximum component should be greyed out by default');
  });

  test('No available settings should be rendered when isEnabled flag is false', async function(assert) {
    const newAvailableSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: false, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz().build().usm.policyWizard;
    setStateOldSchool({ ...initialState, availableSettings: newAvailableSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.available-settings .available-setting').length, 0, 'No available settings should be rendered when isEnabled flag is false');
  });

  test('No other selected settings should be rendered when scanScheduleId is not in the selected settings ', async function(assert) {
    const newSelectedSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    const initialState = new ReduxDataHelper(/* setState */).policyWiz().build().usm.policyWizard;
    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy-wizard/define-policy-step}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 2, 'All selected settings rendered on the UI');
    // simulate a click on the minus icon next to scan schedule.
    // this triggers RESET_SCAN_SCHEDULE_TO_DEFAULTS reducer and clears out all selected settings
    const minusIcon = document.querySelector('.scan-schedule span .rsa-icon');
    await click(minusIcon);
    assert.equal(findAll('.selected-settings .selected-setting').length, 0, 'No other selected settings are rendered when scanSchedule is not rendered');
  });

});
