import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, click, find, findAll, fillIn, focus, render, triggerEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';
import {
  policy,
  selectedSourceType
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

let redux, setState;

module('Integration | Component | usm-policies/policy-wizard/identify-policy-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n', 'service:features');
    setState = (state) => {
      patchReducer(this, state);
      redux = this.owner.lookup('service:redux');
    };
  });

  test('The component appears in the DOM', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.identify-policy-step').length, 1, 'The component appears in the DOM');

    // power-select does not seem to render added class(es) like power-select-multiple,
    // so .source-type was added to the control wrapper div
    assert.equal(findAll('.control.source-type .ember-power-select-selected-item').length, 1, 'Source Type power-select control appears in the DOM');
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    assert.equal(findAll('.control-with-error .policy-description textarea').length, 1, 'Policy Description input control appears in the DOM');
  });

  test('The policy-name, policy-description, and source-type components are enabled correctly for default policy item', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType('edrPolicy') // the ID since it's a power-select
      .policyWizName('test name')
      .policyWizDescription('test desc')
      .policyWizDefaultPolicy(true)
      .policyWizCreatedOn(0)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.identify-policy-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type .ember-power-select-trigger[aria-disabled=true]').length, 1, 'Source Type power-select control appears in the DOM and is disabled');
    assert.equal(findAll('.policy-name')[0].classList.contains('is-disabled'), true, 'Policy Name input control appears in the DOM and is disabled');
    assert.equal(findAll('.policy-description')[0].classList.contains('is-disabled'), true, 'Policy Description input control appears in the DOM and is disabled');
  });

  test('The policy-name, policy-description, and source-type components are are enabled correctly for non default new policy item', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType('edrPolicy') // the ID since it's a power-select
      .policyWizName('test name')
      .policyWizDescription('test desc')
      .policyWizDefaultPolicy(false)
      .policyWizCreatedOn(0)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.identify-policy-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type .ember-power-select-trigger[aria-disabled=true]').length, 0, 'Source Type power-select control appears in the DOM and is enabled');
    assert.equal(findAll('.policy-name')[0].classList.contains('is-disabled'), false, 'Policy Name input control appears in the DOM and is enabled');
    assert.equal(findAll('.policy-description')[0].classList.contains('is-disabled'), false, 'Policy Description input control appears in the DOM and is enabled');
  });

  test('The policy-name, policy-description, and source-type components are enabled correctly for non default existing policy', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType('edrPolicy') // the ID since it's a power-select
      .policyWizName('test name')
      .policyWizDescription('test desc')
      .policyWizDefaultPolicy(false)
      .policyWizCreatedOn(2)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.identify-policy-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type .ember-power-select-trigger[aria-disabled=true]').length, 1, 'Source Type power-select control appears in the DOM and is disabled');
    assert.equal(findAll('.policy-name')[0].classList.contains('is-disabled'), false, 'Policy Name input control appears in the DOM and is enabled');
    assert.equal(findAll('.policy-description')[0].classList.contains('is-disabled'), false, 'Policy Description input control appears in the DOM and is enabled');
  });

  test('The component appears in the DOM with correct values', async function(assert) {
    assert.expect(3);
    const translation = this.owner.lookup('service:i18n');
    const sourceTypeText = translation.t('adminUsm.policyWizard.edrSourceType');
    const testName = 'test name';
    const testDesc = 'test desc';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType('edrPolicy') // the ID since it's a power-select
      .policyWizName(testName)
      .policyWizDescription(testDesc)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const [sourceTypeEl] = findAll('.control.source-type .ember-power-select-selected-item');
    assert.equal(sourceTypeEl.innerText.trim(), sourceTypeText, `Source Type selection is ${sourceTypeText}`);
    const [nameEl] = findAll('.control .policy-name input');
    assert.equal(nameEl.value, testName, `Policy Name is ${testName}`);
    const [descEl] = findAll('.control-with-error .policy-description textarea');
    assert.equal(descEl.value, testDesc, `Policy Description is ${testDesc}`);
  });

  test('The source type select control should have the correct options', async function(assert) {
    const features = this.owner.lookup('service:features');
    const testName = 'test name';
    const testDesc = 'test desc';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType('edrPolicy') // the ID since it's a power-select
      .policyWizName(testName)
      .policyWizDescription(testDesc)
      .build();

    // filePolicyFeature enabled so all types should be rendered
    // & allowFilePolicies enabled so filePolicy type should be enabled
    features.setFeatureFlags({ 'rsa.usm.filePolicyFeature': true });
    features.setFeatureFlags({ 'rsa.usm.allowFilePolicies': true });
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    await click('.control.source-type .ember-power-select-trigger');
    let sourceTypesAll = findAll('.ember-power-select-option');
    let sourceTypesDisabled = findAll('.ember-power-select-option[aria-disabled=true]');
    assert.equal(sourceTypesAll.length, 3, 'All source types rendered');
    assert.equal(sourceTypesDisabled.length, 0, 'All source types enabled');

    // filePolicyFeature enabled so all types should be rendered
    // & allowFilePolicies disabled so filePolicy type should be disabled
    features.setFeatureFlags({ 'rsa.usm.allowFilePolicies': false });
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    await click('.control.source-type .ember-power-select-trigger');
    sourceTypesAll = findAll('.ember-power-select-option');
    sourceTypesDisabled = findAll('.ember-power-select-option[aria-disabled=true]');
    assert.equal(sourceTypesAll.length, 3, 'All source types rendered');
    assert.equal(sourceTypesDisabled.length, 1, '2 source types enabled, and 1 source type disabled');
    // the filePolicy option should have a tooltip to show why it is disabled
    const expectedFilePolicyDisabledTooltip = 'Endpoint servers need to be on version 11.4 or above to configure log file collection.';
    await triggerEvent('.ember-power-select-option[aria-disabled=true] .tooltip-text', 'mouseover');
    const actualFilePolicyDisabledTooltip = findAll('.tool-tip-value')[0].innerText.trim();
    assert.equal(actualFilePolicyDisabledTooltip, expectedFilePolicyDisabledTooltip, 'disabled filePolicy option tooltip is as expected');

    // filePolicyFeature disabled so filePolicy type should not be rendered
    features.setFeatureFlags({ 'rsa.usm.filePolicyFeature': false });
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    await click('.control.source-type .ember-power-select-trigger');
    sourceTypesAll = findAll('.ember-power-select-option');
    assert.equal(sourceTypesAll.length, 2, '2 source types rendered, and 1 NOT rendered');
  });

  test('Changing the source type select control dispatches the updatePolicyType action creator', async function(assert) {
    const edrPolicyType = 'edrPolicy';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType(edrPolicyType) // the ID since it's a power-select
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const field = 'policyType';
    const expectedValue = 'windowsLogPolicy';
    const translation = this.owner.lookup('service:i18n');
    const optionText = translation.t('adminUsm.policyWizard.windowsLogSourceType');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await selectChoose('.control.source-type', optionText);
    await onChange;
    const actualValue = selectedSourceType(redux.getState());
    assert.equal(actualValue.policyType, expectedValue, `${field} updated from ${edrPolicyType} to ${actualValue.policyType}`);
  });

  test('Changing the source type select does not clear policy name and description', async function(assert) {
    const testName = 'test name';
    const testDesc = 'test desc';
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizSourceType('windowsLogPolicy')
      .policyWizName(testName)
      .policyWizDescription(testDesc)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const translation = this.owner.lookup('service:i18n');
    const sourceTypeText = translation.t('adminUsm.policyWizard.edrSourceType');
    await selectChoose('.source-type', '.ember-power-select-option', 0);
    const [sourceTypeEl] = findAll('.control.source-type .ember-power-select-selected-item');
    assert.equal(sourceTypeEl.innerText.trim(), sourceTypeText, `Source Type selection is ${sourceTypeText}`);
    const [nameEl] = findAll('.control .policy-name input');
    assert.equal(nameEl.value, testName, `Policy Name is ${testName}`);
    const [descEl] = findAll('.control-with-error .policy-description textarea');
    assert.equal(descEl.value, testDesc, `Policy Description is ${testDesc}`);
  });

  test('Typing in the policy name control dispatches the editPolicy action creator', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.control.source-type .ember-power-select-selected-item').length, 1, 'Source Type power-select control appears in the DOM');
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    const field = 'name';
    const expectedValue = 'test name';
    const [el] = findAll('.control .policy-name input');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await fillIn(el, expectedValue);
    await triggerEvent(el, 'blur');
    await onChange;
    const actualValue = policy(redux.getState());
    assert.equal(actualValue.name, expectedValue, `${field} updated from '' to ${actualValue.name}`);
  });

  test('Typing in the policy description control dispatches the editPolicy action creator', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    const field = 'description';
    const expectedValue = 'test description';
    const [el] = findAll('.control-with-error .policy-description textarea');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${field}`);
    await fillIn(el, expectedValue);
    await triggerEvent(el, 'blur');
    await onChange;
    const actualValue = policy(redux.getState());
    assert.equal(actualValue.description, expectedValue, `${field} updated from '' to ${actualValue.description}`);
  });

  test('Error message for blank name does not appear if the field has not been visited', async function(assert) {
    assert.expect(1);
    const testName = '';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(testName)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    await assert.notOk(find('.control .policy-name .input-error'), 'Error is not showing');
  });

  test('Error message for blank name appears if the field has been visited', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.nameRequired');
    const testName = '';
    const visitedExpected = ['policy.name'];
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(testName)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.ok(find('.control .policy-name .input-error'), 'Error is showing');
    assert.equal(find('.control .policy-name .input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Error message for name appears when length is too long', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.nameExceedsMaxLength');
    let testName = '';
    for (let index = 0; index < 10; index++) {
      testName += 'the-name-is-greater-than-256-';
    }
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const [el] = findAll('.control .policy-name input');
    await focus(el);
    el.value = testName;
    await blur(el);
    assert.ok(find('.control .policy-name .input-error'), 'Error is showing');
    assert.equal(find('.control .policy-name .input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Error message for description appears when length is too long', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.descriptionExceedsMaxLength');
    let testDesc = '';
    for (let index = 0; index < 220; index++) {
      testDesc += 'the-description-is-greater-than-8000-';
    }
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const [el] = findAll('.control-with-error .policy-description textarea');
    await focus(el);
    el.value = testDesc;
    await blur(el);
    assert.ok(find('.policy-description-error'), 'Error is showing');
    assert.equal(find('.policy-description-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

});
