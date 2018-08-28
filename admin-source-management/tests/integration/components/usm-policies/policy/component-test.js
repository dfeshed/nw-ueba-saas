import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket } from '../../../../helpers/patch-socket';

const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
      scanType: 'MANUAL',
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null,
        scanStartTime: [10, 0],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  policyStatus: null, // wait, complete, error
  availableSettings: [
    { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
    { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
  ],
  selectedSettings: []
};

const policyData = Immutable.from({
  id: 'policy_001',
  name: 'Policy 001',
  description: 'Policy 001 of policy policy_001',
  scheduleConfig: {
    scheduleOptions: {
      scanStartTime: [10, 0]
    }
  }
});

let setState;

module('Integration | Component | usm-policies/policy', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      const fullState = { usm: { policy: state } };
      patchReducer(this, Immutable.from(fullState));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');

  });

  test('Save button is disabled when there is no policy name', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.confirm-button.is-disabled').length, 1, 'The Save button is disabled when there is no policy name');
  });

  test('On failing to save a policy, an error flash message is shown', async function(assert) {
    assert.expect(2);
    setState({ ...initialState, policy: policyData });
    this.set('transitionToPolicies', () => {}); // avoid annoying console error
    await render(hbs`{{usm-policies/policy transitionToPolicies=(action transitionToPolicies)}}`);

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policy.saveFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });

    const el = findAll('.confirm-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(el);
  });

  test('On successfully saving a policy, a success flash message is shown, and the transitionToPolicies action is called', async function(assert) {
    assert.expect(3);
    setState({ ...initialState, policy: policyData });

    const done = assert.async();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policy.saveSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    this.set('transitionToPolicies', () => {
      assert.ok('transition to policy called');
    });

    await render(hbs`{{usm-policies/policy transitionToPolicies=(action transitionToPolicies)}}`);
    const el = findAll('.confirm-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(el);
  });

  test('A loading spinner is displayed if the policyStatus property is "wait"', async function(assert) {
    setState({ ...initialState, policyStatus: 'wait' });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.loading-overlay .rsa-loader').length, 1, 'A loading spinner appears');
  });

  test('All the components in the available settings is rendered on the UI', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.available-settings .available-setting').length, 2, 'All available settings rendered on the UI');
  });

  test('All the components in the selected settings is rendered on the UI ', async function(assert) {
    const newSelectedSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: false, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    setState({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 2, 'All selected settings rendered on the UI');
  });

  test('Effective date component should be greyed out by default', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.available-settings .effectiveDate')[0].classList.contains('is-greyed-out'), true, 'Effective date component should be greyed out by default');
  });

  test('No available settings should be rendered when isEnabled flag is false', async function(assert) {
    const newAvailableSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: false, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: false, isGreyedOut: true, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    setState({ ...initialState, availableSettings: newAvailableSettings });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.available-settings .available-setting').length, 0, 'No available settings should be rendered when isEnabled flag is false');
  });

  test('No other selected settings should be rendered when scanScheduleId is not in the selected settings ', async function(assert) {
    const newSelectedSettings = [
      { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
      { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: false, callback: 'usm-policies/policy/schedule-config/effective-date' }
    ];
    setState({ ...initialState, selectedSettings: newSelectedSettings });
    await render(hbs`{{usm-policies/policy}}`);
    assert.equal(findAll('.selected-settings .selected-setting').length, 2, 'All selected settings rendered on the UI');
    // simulate a click on the minus icon next to scan schedule.
    // this triggers RESET_SCAN_SCHEDULE_TO_DEFAULTS reducer and clears out all selected settings
    const minusIcon = document.querySelector('.scan-schedule span .rsa-icon');
    await click(minusIcon);
    assert.equal(findAll('.selected-settings .selected-setting').length, 0, 'No other selected settings are rendered when scanSchedule is not rendered');
  });

});
