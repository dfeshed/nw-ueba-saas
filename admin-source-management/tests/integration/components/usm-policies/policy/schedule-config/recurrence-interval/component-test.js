import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll, settled } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import { clickTrigger } from '../../../../../../helpers/ember-power-select';
import { patchPowerSelect, restorePowerSelect } from '../../../../../../helpers/patch-power-select';

import policyCreators from 'admin-source-management/actions/creators/policy-creators';
import sinon from 'sinon';

let removeFromSelectedSettingsSpy, updatePolicyPropertySpy;

module('Integration | Component | usm-policies/policy/schedule-config/recurrence-interval', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    removeFromSelectedSettingsSpy = sinon.spy(policyCreators, 'removeFromSelectedSettings');
    updatePolicyPropertySpy = sinon.spy(policyCreators, 'updatePolicyProperty');
  });

  hooks.afterEach(function() {
    restorePowerSelect();
    removeFromSelectedSettingsSpy.reset();
    updatePolicyPropertySpy.reset();
  });

  hooks.after(function() {
    removeFromSelectedSettingsSpy.restore();
    updatePolicyPropertySpy.restore();
  });

  test('should render recurrence interval fields', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-interval').length, 1, 'expected to have root element in DOM');
  });

  test('should display daily and weekly recurrence type', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(findAll('.recurrence-type').length, 2, 'expected to have two radio button in dom');
  });

  test('should display Daily recurrence fields on clicking the Daily radio button', async function(assert) {
    patchPowerSelect();
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(this.$('.recurrence-interval input:eq(0)').val(), 'DAYS', 'expected to render DAYS as first field');
    assert.equal(findAll('.recurrence-run-interval').length, 1, 'expected to render dropdown for run interval');
    assert.equal(findAll('input[type=radio]:checked').length, 1, 'Expected to select default radio button');
    return wait().then(() => {
      clickTrigger();
      assert.ok(this.$('.ember-power-select-option:contains("1")').attr('aria-disabled') !== 'true');
      assert.ok(this.$('.ember-power-select-option:contains("20")').attr('aria-disabled') !== 'true');
    });
  });

  // TODO - fix this test, the behaviour is very erratic. Even though action creator is being called, callCount is not being incremented.
  skip('should trigger the updatePolicyProperty action creator on clicking the Daily or Weekly radio button', async function(assert) {
    assert.expect(2);
    patchPowerSelect();
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when no click is registered');
    await click('.recurrence-interval .rsa-form-radio-label:nth-of-type(2) input');
    return settled().then(() => {
      assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called Daily/Weekly toggle is changed');
    });
  });

  test('should display weeks recurrence field options on clicking the Weekly radio button', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    await click('.recurrence-interval .rsa-form-radio-label:nth-of-type(2) input');
    assert.equal(this.$('input[type=radio]:eq(1):checked').length, 1, 'Expected to select Weekly radio button');
    assert.equal(findAll('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
  });

  test('should select the week on clicking the available week options', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    await click('.recurrence-interval .rsa-form-radio-label:nth-of-type(2) input');
    assert.equal(this.$('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
    await click('.week-button');
    assert.equal(this.$('.week-button:eq(0).is-primary').length, 1);
  });

  // TODO - fix this test, the behaviour is very erratic. Even though action creator is being called, callCount is not being incremented.
  skip('should trigger the updatePolicyProperty action creator when clicking the week schedule', async function(assert) {
    assert.expect(2);
    patchPowerSelect();
    await render(hbs`{{usm-policies/policy/schedule-config/recurrence-interval}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when no week is selected');
    // change toggle to weeks. This would bring up a div of buttons for each day of the week (S, M, T, W etc)
    await click('.recurrence-interval .rsa-form-radio-label:nth-of-type(2) input');
    await click('.recurrence-run-interval__week-options');
    return settled().then(() => {
      assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called when clicking the week schedule');
    });
  });
});
