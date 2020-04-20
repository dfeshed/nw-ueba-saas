import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click, fillIn, triggerEvent } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/signature-dropdown', function(hooks) {
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

  test('Should render signature-dropdown', async function(assert) {
    const policy = {
      id: '5d91e6fada7bd9033284f6d9',
      policyType: 'edrPolicy',
      name: 'test',
      isolationEnabled: true,
      fileDownloadCriteria: 'Unsigned'
    };
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPolicy(policy)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/signature-dropdown selectedSettingId='fileDownloadCriteria'}}`);
    assert.equal(findAll('.signature-dropdown .ember-power-select-trigger').length, 1, 'Renders the dropdown list');
  });

  test('It triggers the update policy action creator when the dropdown value is changed', async function(assert) {
    const policy = {
      id: '5d91e6fada7bd9033284f6d9',
      policyType: 'edrPolicy',
      name: 'test',
      isolationEnabled: true,
      fileDownloadCriteria: 'Unsigned'
    };
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPolicy(policy)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/signature-dropdown selectedSettingId='fileDownloadCriteria'}}`);
    await selectChoose('.signature-dropdown', '.ember-power-select-option', 0);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('Dropdown renders the list', async function(assert) {
    const policy = {
      id: '5d91e6fada7bd9033284f6d9',
      policyType: 'edrPolicy',
      name: 'test',
      isolationEnabled: true,
      fileDownloadCriteria: 'Unsigned'
    };
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPolicy(policy)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/signature-dropdown selectedSettingId='fileDownloadCriteria'}}`);
    await click('.ember-power-select-trigger');

    assert.equal(findAll('.ember-power-select-options li').length, 3, 'List of dropdown options.');
  });

  // File size selection
  test('should render file-size-selection component', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/file-size-selection selectedSettingId='maxFileDownloadSize'}}`);
    assert.equal(findAll('.file-size-selection .beacon-interval-value').length, 1, 'Size value present');
    assert.equal(findAll('.file-size-selection .beacon-interval-unit').length, 1, 'Size value unit present');
  });

  test('should render maxFileDownloadSize component when id is maxFileDownloadSize', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/file-size-selection selectedSettingId='maxFileDownloadSize'}}`);
    assert.equal(findAll('.maxFileDownloadSize').length, 1, 'expected to have maxFileDownloadSize root input element in DOM');
  });

  test('maxFileDownloadSize appears in the DOM with correct values', async function(assert) {
    assert.expect(3);
    const translation = this.owner.lookup('service:i18n');
    const size = 15;
    const sizeUnit = 'MB';
    const intervalUnitText = translation.t('adminUsm.policyWizard.edrPolicy.maxFileDownloadSize_MB');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizMaxFileDownloadSize(size)
      .policyWizMaxFileDownloadSizeUnit(sizeUnit)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/file-size-selection selectedSettingId='maxFileDownloadSize'}}`);

    const [intervalEl] = findAll('.maxFileDownloadSize .beacon-interval-value input');
    assert.equal(intervalEl.value, size, `maxFileDownloadSize is ${size}`);
    const [unitEl] = findAll('.maxFileDownloadSize .beacon-interval-unit .ember-power-select-selected-item');
    assert.equal(unitEl.innerText.trim(), intervalUnitText, `maxFileDownloadSizeUnit selection is ${intervalUnitText}`);
    assert.equal(find('.input-error').textContent.trim(), 'File size should be between 1 KB - 10 MB', 'Input error present as size is more than 10MB');
  });

  test('maxFileDownloadSize appears in the DOM with correct values', async function(assert) {
    assert.expect(3);
    const translation = this.owner.lookup('service:i18n');
    const size = 1;
    const sizeUnit = 'MB';
    const intervalUnitText = translation.t('adminUsm.policyWizard.edrPolicy.maxFileDownloadSize_MB');
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizMaxFileDownloadSize(size)
      .policyWizMaxFileDownloadSizeUnit(sizeUnit)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/file-size-selection selectedSettingId='maxFileDownloadSize'}}`);
    const [intervalEl] = findAll('.maxFileDownloadSize .beacon-interval-value input');
    assert.equal(intervalEl.value, size, `maxFileDownloadSize is ${size}`);
    const [unitEl] = findAll('.maxFileDownloadSize .beacon-interval-unit .ember-power-select-selected-item');
    assert.equal(unitEl.innerText.trim(), intervalUnitText, `maxFileDownloadSizeUnit selection is ${intervalUnitText}`);
    assert.equal(findAll('.input-error').length, 0, 'Input error not present as size is less than 10MB');
  });

  test('It triggers the update policy action creator when the maxFileDownloadSize is changed', async function(assert) {
    const size = 1;
    const sizeUnit = 'MB';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizMaxFileDownloadSize(size)
      .policyWizMaxFileDownloadSizeUnit(sizeUnit)
      .build();
    const selectedSettingId = 'maxFileDownloadSize';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/file-size-selection selectedSettingId=selectedSettingId}}`);
    assert.equal(findAll('.input-error').length, 0, 'Input error not present as size is less than 10MB');

    const field = 'maxFileDownloadSize';
    const value = 10.1;
    const [intervalEl] = findAll('.maxFileDownloadSize .beacon-interval-value input');

    await fillIn(intervalEl, value);
    await triggerEvent(intervalEl, 'blur');

    const actualValue = find('.beacon-interval-value input').value;
    assert.equal(actualValue, value, `${field} updated from ${size} to ${actualValue}`);
    assert.equal(find('.input-error').textContent.trim(), 'File size should be between 1 KB - 10 MB', 'Input error present as size is more than 10MB');
  });

  test('It triggers the update policy action creator when the maxFileDownloadSizeUnit is changed', async function(assert) {
    const size = 15.6;
    const sizeUnit = 'KB';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizMaxFileDownloadSize(size)
      .policyWizMaxFileDownloadSizeUnit(sizeUnit)
      .build();
    const selectedSettingId = 'maxFileDownloadSize';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/file-size-selection selectedSettingId=selectedSettingId}}`);

    assert.equal(findAll('.input-error').length, 0, 'Input error not present as size is less than 10MB');

    const field = 'maxFileDownloadSizeUnit';
    const value = 'MB';
    const translation = this.owner.lookup('service:i18n');
    const optionText = translation.t('adminUsm.policyWizard.edrPolicy.maxFileDownloadSize_MB');

    await selectChoose('.maxFileDownloadSize .beacon-interval-unit', optionText);
    await triggerEvent(find('.beacon-interval-unit'), 'blur');

    const actualValue = find('.beacon-interval-unit .ember-power-select-selected-item').textContent.trim();
    assert.equal(actualValue, value, `${field} updated from ${sizeUnit} to ${actualValue.unit}`);
    assert.equal(find('.input-error').textContent.trim(), 'File size should be between 1 KB - 10 MB', 'Input error present now as size is more than 10MB');
  });


});