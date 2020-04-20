import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../../../helpers/redux-async-helpers';
import {
  intervalType,
  runOnDaysOfWeek
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

let redux, setState;

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/recurrence-interval', function(hooks) {
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

  test('should render recurrence interval fields', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-interval').length, 1, 'expected to have root element in DOM');
  });

  test('should display daily and weekly recurrence type', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-type').length, 2, 'expected to have two radio button in dom');
  });

  test('should display Daily recurrence fields on clicking the Daily radio button', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizRecurrenceInterval(1)
      .policyWizRecurrenceUnit('DAYS')
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-interval input')[0].value, 'DAYS', 'expected to render DAYS as first field');
    assert.equal(findAll('.recurrence-run-interval').length, 1, 'expected to render dropdown for run interval');
    assert.equal(findAll('input[type=radio]:checked').length, 1, 'Expected to select default radio button');
    await clickTrigger();
    // text value of 1
    assert.ok(findAll('.ember-power-select-option')[0].getAttribute('aria-disabled') !== 'true');
    // text value of 20
    assert.ok(findAll('.ember-power-select-option')[8].getAttribute('aria-disabled') !== 'true');
  });

  test('should trigger the updatePolicyProperty action creator on clicking the Daily or Weekly radio button', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizRecurrenceInterval(1)
      .policyWizRecurrenceUnit('DAYS')
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    const field1 = 'recurrenceUnit';
    const expectedValue1 = 'WEEKS';
    const field2 = 'runOnDaysOfWeek';
    const expectedValue2 = ['MONDAY']; // should be the default
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field1}`);
    await click('.recurrence-interval .rsa-form-radio-wrapper:nth-of-type(2) input');
    await onChange;
    const actualValue1 = intervalType(redux.getState());
    const actualValue2 = runOnDaysOfWeek(redux.getState());
    assert.deepEqual(actualValue1, expectedValue1, `${field1} updated to ${actualValue1}`);
    assert.deepEqual(actualValue2, expectedValue2, `${field2} updated to ${actualValue2}`);
  });

  test('should display weeks recurrence field options on clicking the Weekly radio button', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    await click('.recurrence-interval .rsa-form-radio-wrapper:nth-of-type(2) input');
    assert.equal(findAll('input[type=radio]')[1].checked, true, 'Expected to select Weekly radio button');
    assert.equal(findAll('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
  });

  test('should select the week on clicking the available week options', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    await click('.recurrence-interval .rsa-form-radio-wrapper:nth-of-type(2) input');
    assert.equal(findAll('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
    assert.equal(findAll('.week-button')[1].classList.contains('is-primary'), true, 'By default Monday is selected');
    await click('.recurrence-interval .rsa-form-radio-wrapper:nth-of-type(1) input'); // select days
    await click('.recurrence-interval .rsa-form-radio-wrapper:nth-of-type(2) input'); // select week again
    assert.equal(findAll('.week-button')[1].classList.contains('is-primary'), true, 'Default week selection is retained while switching between days and weeks');
  });

  test('should trigger the updatePolicyProperty action creator when clicking the week schedule', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizRecurrenceInterval(1)
      .policyWizRunOnDaysOfWeek(['MONDAY'])
      .policyWizRecurrenceUnit('WEEKS')
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/recurrence-interval}}`);
    const field = 'runOnDaysOfWeek';
    const expectedValue = ['WEDNESDAY'];
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await click('.recurrence-run-interval__week-options .week-button:nth-of-type(4) button');
    await onChange;
    const actualValue = runOnDaysOfWeek(redux.getState());
    assert.deepEqual(actualValue, expectedValue, `${field} updated to ${actualValue}`);
  });
});
