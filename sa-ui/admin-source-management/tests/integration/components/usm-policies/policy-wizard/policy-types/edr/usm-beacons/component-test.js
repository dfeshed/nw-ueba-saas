import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, find, findAll, fillIn, triggerEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
// import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../../../helpers/redux-async-helpers';
// import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  beaconIntervalValue,
  selectedBeaconIntervalUnit
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

let redux, setState; /* , updatePolicyPropertySpy; */
// const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/edr-beacons', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  // hooks.before(function() {
  //   spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  // });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
      redux = this.owner.lookup('service:redux');
    };
  });

  // hooks.afterEach(function() {
  //   spys.forEach((s) => s.resetHistory());
  // });

  // hooks.after(function() {
  //   spys.forEach((s) => s.restore());
  // });

  test('should render primaryHttpsBeaconInterval component when id is primaryHttpsBeaconInterval', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId='primaryHttpsBeaconInterval'}}`);
    assert.equal(findAll('.primaryHttpsBeaconInterval').length, 1, 'expected to have primaryHttpsBeaconInterval root input element in DOM');
  });

  test('should render primaryUdpBeaconInterval component when id is primaryUdpBeaconInterval', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId='primaryUdpBeaconInterval'}}`);
    assert.equal(findAll('.primaryUdpBeaconInterval').length, 1, 'expected to have primaryUdpBeaconInterval root input element in DOM');
  });

  test('primaryHttpsBeaconInterval appears in the DOM with correct values', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const httpsInterval = 15;
    const intervalUnit = 'MINUTES';
    const intervalUnitText = translation.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval_MINUTES');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(httpsInterval)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId='primaryHttpsBeaconInterval'}}`);
    //
    // const actualPrimaryHttpsBeaconInterval = redux.getState().usm.policyWizard.policy.primaryHttpsBeaconInterval;
    // assert.equal(actualPrimaryHttpsBeaconInterval, httpsInterval, `primaryHttpsBeaconInterval from redux should be ${httpsInterval}`);
    // const actualPrimaryHttpsBeaconIntervalUnit = redux.getState().usm.policyWizard.policy.primaryHttpsBeaconIntervalUnit;
    // assert.equal(actualPrimaryHttpsBeaconIntervalUnit, intervalUnit, `primaryHttpsBeaconIntervalUnit from redux should be ${intervalUnit}`);
    //
    const [intervalEl] = findAll('.primaryHttpsBeaconInterval .beacon-interval-value input');
    assert.equal(intervalEl.value, httpsInterval, `primaryHttpsBeaconInterval is ${httpsInterval}`);
    const [unitEl] = findAll('.primaryHttpsBeaconInterval .beacon-interval-unit .ember-power-select-selected-item');
    assert.equal(unitEl.innerText.trim(), intervalUnitText, `primaryHttpsBeaconIntervalUnit selection is ${intervalUnitText}`);
  });

  test('primaryUdpBeaconInterval appears in the DOM with correct values', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const udpInterval = 30;
    const intervalUnit = 'SECONDS';
    const intervalUnitText = translation.t('adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval_SECONDS');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(udpInterval)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId='primaryUdpBeaconInterval'}}`);
    const [intervalEl] = findAll('.primaryUdpBeaconInterval .beacon-interval-value input');
    assert.equal(intervalEl.value, udpInterval, `primaryUdpBeaconInterval is ${udpInterval}`);
    const [unitEl] = findAll('.primaryUdpBeaconInterval .beacon-interval-unit .ember-power-select-selected-item');
    assert.equal(unitEl.innerText.trim(), intervalUnitText, `primaryUdpBeaconIntervalUnit selection is ${intervalUnitText}`);
  });

  test('It triggers the update policy action creator when the primaryHttpsBeaconInterval is changed', async function(assert) {
    const httpsInterval = 15;
    const intervalUnit = 'MINUTES';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(httpsInterval)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .build();
    const selectedSettingId = 'primaryHttpsBeaconInterval';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId=selectedSettingId}}`);
    const field = 'primaryHttpsBeaconInterval';
    const value = 20;
    const [intervalEl] = findAll('.primaryHttpsBeaconInterval .beacon-interval-value input');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await fillIn(intervalEl, value);
    await triggerEvent(intervalEl, 'blur');
    await onChange;
    // assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
    // assert.equal(updatePolicyPropertySpy.calledWith(field, value), true, 'Update policy property action creator was called with expected args');
    const actualValue = beaconIntervalValue(redux.getState(), selectedSettingId);
    assert.equal(actualValue, value, `${field} updated from ${httpsInterval} to ${actualValue}`);
  });

  test('It triggers the update policy action creator when the primaryHttpsBeaconIntervalUnit is changed', async function(assert) {
    const httpsInterval = 15;
    const intervalUnit = 'MINUTES';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(httpsInterval)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .build();
    const selectedSettingId = 'primaryHttpsBeaconInterval';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId=selectedSettingId}}`);
    const field = 'primaryHttpsBeaconIntervalUnit';
    const value = 'HOURS';
    const translation = this.owner.lookup('service:i18n');
    const optionText = translation.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval_HOURS');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await selectChoose('.primaryHttpsBeaconInterval .beacon-interval-unit', optionText);
    await onChange;
    // assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
    // assert.equal(updatePolicyPropertySpy.calledWith(field, value), true, 'Update policy property action creator was called with expected args');
    const actualValue = selectedBeaconIntervalUnit(redux.getState(), selectedSettingId);
    assert.equal(actualValue.unit, value, `${field} updated from ${intervalUnit} to ${actualValue.unit}`);
  });

  test('It triggers the update policy action creator when the primaryUdpBeaconInterval is changed', async function(assert) {
    const udpInterval = 30;
    const intervalUnit = 'SECONDS';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(udpInterval)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .build();
    const selectedSettingId = 'primaryUdpBeaconInterval';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId=selectedSettingId}}`);
    const field = 'primaryUdpBeaconInterval';
    const value = 20;
    const [intervalEl] = findAll('.primaryUdpBeaconInterval .beacon-interval-value input');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await fillIn(intervalEl, value);
    await triggerEvent(intervalEl, 'blur');
    await onChange;
    // assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
    // assert.equal(updatePolicyPropertySpy.calledWith(field, value), true, 'Update policy property action creator was called with expected args');
    const actualValue = beaconIntervalValue(redux.getState(), selectedSettingId);
    assert.equal(actualValue, value, `${field} updated from ${udpInterval} to ${actualValue}`);
  });

  test('It triggers the update policy action creator when the primaryUdpBeaconIntervalUnit is changed', async function(assert) {
    const udpInterval = 30;
    const intervalUnit = 'SECONDS';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(udpInterval)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .build();
    const selectedSettingId = 'primaryUdpBeaconInterval';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId=selectedSettingId}}`);
    const field = 'primaryUdpBeaconIntervalUnit';
    const value = 'MINUTES';
    const translation = this.owner.lookup('service:i18n');
    const optionText = translation.t('adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval_MINUTES');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await selectChoose('.primaryUdpBeaconInterval .beacon-interval-unit', optionText);
    await onChange;
    // assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
    // assert.equal(updatePolicyPropertySpy.calledWith(field, value), true, 'Update policy property action creator was called with expected args');
    const actualValue = selectedBeaconIntervalUnit(redux.getState(), selectedSettingId);
    assert.equal(actualValue.unit, value, `${field} updated from ${intervalUnit} to ${actualValue.unit}`);
  });

  test('It shows the error message when the primaryHttpsBeaconInterval is invalid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const httpsInterval = 25; // valid range is 1 minute to 24 hours  (or 1440 minutes or 86400 seconds)
    const intervalUnit = 'HOURS';
    const visitedExpected = ['policy.primaryHttpsBeaconInterval'];
    const expectedMessage = translation.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconIntervalInvalidMsg');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(httpsInterval)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId='primaryHttpsBeaconInterval'}}`);
    assert.ok(find('.primaryHttpsBeaconInterval .beacon-interval-value .input-error'), 'Error is showing');
    assert.equal(find('.primaryHttpsBeaconInterval .beacon-interval-value .input-error').innerText.trim(), expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });

  test('It shows the error message when the primaryUdpBeaconInterval is invalid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const udpInterval = 11; // valid range is 5 seconds to 10 minutes (600 seconds)
    const intervalUnit = 'MINUTES';
    const visitedExpected = ['policy.primaryUdpBeaconInterval'];
    const expectedMessage = translation.t('adminUsm.policyWizard.edrPolicy.primaryUdpBeaconIntervalInvalidMsg');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(udpInterval)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/edr-beacons selectedSettingId='primaryUdpBeaconInterval'}}`);
    assert.ok(find('.primaryUdpBeaconInterval .beacon-interval-value .input-error'), 'Error is showing');
    assert.equal(find('.primaryUdpBeaconInterval .beacon-interval-value .input-error').innerText.trim(), expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });

});
