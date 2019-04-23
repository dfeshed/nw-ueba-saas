import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket } from '../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { blur, click, fillIn, find, render } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import sinon from 'sinon';
import ruleInfo from '../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../data/subscriptions/incident-fields/findAll/data';
import categoryTags from '../../../../data/subscriptions/category-tags/findAll/data';
import enabledUsers from '../../../../data/subscriptions/users/findAll/data';
import * as incidentRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';

const initialState = {
  ruleInfo,
  ruleStatus: 'complete',
  fields,
  fieldsStatus: 'complete',
  isTransactionUnderway: false,
  visited: []
};

const selectors = {
  loadSpinner: '.rsa-incident-rule .rsa-loader',
  form: '.rsa-incident-rule .incident-rule-form',
  nameInput: '.rule-control.name input',
  enabledInput: '.rule-control.enabled input',
  descriptionInput: '.rule-control.description textarea',
  queryMode: '.rule-control.match-conditions .query-type',
  queryModeDropdown: '.rule-control.match-conditions .query-type .ember-power-select-trigger',
  queryModeConfirmationModal: '.respond-confirmation-dialog',
  advancedQuery: '.rule-control.match-conditions textarea',
  ruleBuilder: '.rule-control.match-conditions .rsa-rule-builder',
  groupIntoIncidentActionInput: '.rule-control.action input[value="GROUP_INTO_INCIDENT"]',
  suppressAlertActionInput: '.rule-control.action input[value="SUPPRESS_ALERT"]',
  groupingOptions: '.incident-rule-grouping-options',
  transactionOverlay: '.transaction-overlay',
  missingInformationWarning: 'footer .missing-information-warning',
  saveButton: 'footer .form-save-controls .confirm-button button',
  formWarning: 'footer .form-warning'
};

const spyRuleUpdate = async function({ state, assert, selector }) {
  assert.expect(1);
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState(state || { ...initialState });
  await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  await fillIn(selector, 'To rule is divine');
  await blur(selector);
  assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
  actionSpy.restore();
};

const findModal = (selector) => {
  return document.querySelector(`#modalDestination ${selector}`);
};

let setState;

module('Integration | Component | Respond Incident Rule', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = {
        configure: {
          respond: {
            incidentRule: state,
            dictionaries: { categoryTags },
            users: { enabledUsers }
          }
        }
      };
      patchReducer(this, fullState);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find(selectors.form), 'The component appears in the DOM');
  });

  test('A loading spinner appears if the data is still loading', async function(assert) {
    setState({ ...initialState, ruleStatus: 'wait' });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find(selectors.loadSpinner), 'The spinner appears');
    assert.notOk(find(selectors.form), 'The form is gone');
  });

  test('The form displays the expected data in the form fields', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find(selectors.enabledInput).checked, 'The enabled checkbox is checked');
    assert.equal(find(selectors.nameInput).value, 'Thou shalt not break this rule', 'The name appears in the name input');
    assert.equal(find(selectors.descriptionInput).value, 'Any fool can make a rule. And any fool will mind it.', 'The description appears in the textarea');
    assert.equal(find(selectors.queryModeDropdown).textContent.trim(), 'Rule Builder', 'The query mode dropdown is Rule Builder');
    assert.ok(find(selectors.ruleBuilder), 'The Rule Builder is present');
    assert.ok(find(selectors.groupIntoIncidentActionInput).checked, 'The group-into-incident radio is checked');
    assert.ok(find(selectors.groupingOptions), 'The grouping options are displayed since the action is group-into-incident');
  });

  test('The grouping options are not displayed if the action is SUPPRESS_ALERT', async function(assert) {
    setState({
      ...initialState,
      ruleInfo: {
        ...initialState.ruleInfo,
        action: 'SUPPRESS_ALERT'
      }
    });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find(selectors.suppressAlertActionInput).checked, 'The supress alert radio is checked');
    assert.notOk(find(selectors.groupingOptions), 'The grouping options are not displayed since the action is suppress-alert');
  });

  test('The advanced query mode dropdown and text area appears as expected if advanedUIFilterConditions is true in ruleInfo', async function(assert) {
    setState({
      ...initialState,
      ruleInfo: {
        ...initialState.ruleInfo,
        advancedUiFilterConditions: true
      }
    });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.equal(find(selectors.queryModeDropdown).textContent.trim(), 'Advanced', 'The query mode dropdown is Advanced');
    assert.notOk(find(selectors.ruleBuilder), 'The Rule Builder is not present');
    assert.ok(find(selectors.advancedQuery), 'The Advanced Query textarea is present');
  });

  test('Changing the query mode displays a confirmation dialog. Canceling it keeps the Rule Builder query mode', async function(assert) {
    setState({ ...initialState });
    await render(hbs`
      <div id='modalDestination'></div>
      {{respond/incident-rule ruleId='12345'}}
    `);
    assert.equal(find(selectors.queryModeDropdown).textContent.trim(), 'Rule Builder', 'The query mode dropdown is Rule Builder');
    await selectChoose(selectors.queryMode, 'Advanced');
    assert.ok(findModal(selectors.queryModeConfirmationModal), 'The confirmation modal appears');
    await click(`${selectors.queryModeConfirmationModal} .cancel-button .rsa-form-button`);
    assert.notOk(find(`#modalDestination ${selectors.queryModeConfirmationModal}`), 'The confirmation modal is gone');
    assert.ok(find(selectors.ruleBuilder), 'The Rule Builder is present');
  });

  test('Confirming the query mode change dispatches the updateRule creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`
      <div id='modalDestination'></div>
      {{respond/incident-rule ruleId='12345'}}
    `);
    await selectChoose(selectors.queryMode, 'Advanced');
    assert.ok(findModal(selectors.queryModeConfirmationModal), 'The confirmation modal appears');
    await click(`${selectors.queryModeConfirmationModal} .confirm-button .rsa-form-button`);
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Clicking the action radio button dispatches the updateRule creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    await click(selectors.suppressAlertActionInput);
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Changing the name dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.nameInput });
  });

  test('Changing the description dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.descriptionInput });
  });

  test('Changing the advanced query dispatches the updateRule creator', async function(assert) {
    const state = { ...initialState, ruleInfo: { ...initialState.ruleInfo, advancedUiFilterConditions: true } };
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.advancedQuery, state });
  });

  test('Error message for name does not appear if the field has not been visited', async function(assert) {
    setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, name: '' } });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.notOk(find('.rule-control.name .input-error'), 'There is no error message if the name has not been visited');
  });

  test('Error message for name does appear if the field has been visited', async function(assert) {
    setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, name: '' }, visited: ['ruleInfo.name'] });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find('.rule-control.name .input-error'), 'There is an error message if the name has been visited');
  });

  test('Overlay appears if a transaction is underway', async function(assert) {
    setState({ ...initialState, isTransactionUnderway: true });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find(selectors.transactionOverlay), 'There is a transaction overlay if isTransactionUnderway is true');
  });

  test('Footer Save Button is disabled and warning message is displayed when missing information', async function(assert) {
    const i18n = this.owner.lookup('service:i18n');
    setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, name: '' } });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    const warningMessage = i18n.t('configure.incidentRules.missingRequiredInfo').toString();
    assert.equal(find(selectors.formWarning).textContent.trim(), warningMessage, 'There is a missing information warning displayed in the footer');
    assert.ok(find(selectors.saveButton).disabled, 'The Save button is disabled');
  });

  test('Footer Save Button is disabled and warning message is displayed when user lacks permission to edit page', async function(assert) {
    setState({ ...initialState });
    const translation = this.owner.lookup('service:i18n');
    const warningMessage = translation.t('configure.incidentRules.noManagePermissions');
    const accessControl = this.owner.lookup('service:accessControl');
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.notOk(find(selectors.saveButton).disabled, 'The Save button is not disabled');
    accessControl.set('roles', []);
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.ok(find(selectors.saveButton).disabled, 'The Save button is disabled');
    assert.equal(find(selectors.formWarning).textContent.trim(), warningMessage, 'A warning is displayed to users that they have no permissions to edit');
  });

  // GTB SKIP
  skip('If an error occurs during "Save", a general save error flash message is displayed to the user', async function(assert) {
    assert.expect(3);
    setState({ ...initialState });
    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('configure.incidentRules.actionMessages.saveFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });
    await render(hbs`{{respond/incident-rule ruleId='12345'}}`);
    assert.notOk(find(selectors.saveButton).disabled, 'The Save button is not disabled');
    await click(selectors.saveButton);
  });
});