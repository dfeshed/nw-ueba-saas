import { moduleForComponent, test } from 'ember-qunit';
import { getOwner } from '@ember/application';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';
import ruleInfo from '../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../data/subscriptions/incident-fields/findAll/data';
import categoryTags from '../../../../data/subscriptions/category-tags/findAll/data';
import enabledUsers from '../../../../data/subscriptions/users/findAll/data';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';
import { click, fillIn, find, triggerEvent } from 'ember-native-dom-helpers';
import $ from 'jquery';
import * as incidentRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';

const initialState = {
  ruleInfo,
  ruleStatus: 'complete',
  fields,
  fieldsStatus: 'complete',
  isTransactionUnderway: false,
  visited: []
};

let setState;
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

moduleForComponent('respond/incident-rule', 'Integration | Component | Respond Incident Rule', {
  integration: true,
  resolver: engineResolverFor('configure'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
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
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
    this.inject.service('accessControl');
  },
  afterEach() {
    revertPatch();
  }
});

const spyRuleUpdate = ({ state, test, assert, selector }) => {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState(state || { ...initialState });
  test.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  fillIn(selector, 'To rule is divine');
  triggerEvent(selector, 'blur');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });
};

test('The component appears in the DOM', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.ok(find(selectors.form), 'The component appears in the DOM');
});

test('A loading spinner appears if the data is still loading', function(assert) {
  setState({ ...initialState, ruleStatus: 'wait' });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.ok(find(selectors.loadSpinner), 'The spinner appears');
  assert.notOk(find(selectors.form), 'The form is gone');
});

test('The form displays the expected data in the form fields', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.equal(this.$(selectors.enabledInput).is(':checked'), true, 'The enabled checkbox is checked');
  assert.equal(this.$(selectors.nameInput).val(), 'Thou shalt not break this rule', 'The name appears in the name input');
  assert.equal(this.$(selectors.descriptionInput).val(), 'Any fool can make a rule. And any fool will mind it.', 'The description appears in the textarea');
  assert.equal(this.$(selectors.queryModeDropdown).text().trim(), 'Rule Builder', 'The query mode dropdown is Rule Builder');
  assert.ok(find(selectors.ruleBuilder), 'The Rule Builder is present');
  assert.equal(this.$(selectors.groupIntoIncidentActionInput).is(':checked'), true, 'The group-into-incident radio is checked');
  assert.ok(find(selectors.groupingOptions), 'The grouping options are displayed since the action is group-into-incident');
});

test('The grouping options are not displayed if the action is SUPPRESS_ALERT', function(assert) {
  setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, action: 'SUPPRESS_ALERT' } });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.equal(this.$(selectors.suppressAlertActionInput).is(':checked'), true, 'The supress alert radio is checked');
  assert.notOk(find(selectors.groupingOptions), 'The grouping options are not displayed since the action is suppress-alert');
});

test('The advanced query mode dropdown and text area appears as expected if advanedUIFilterConditions is true in ruleInfo', function(assert) {
  setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, advancedUiFilterConditions: true } });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.equal(this.$(selectors.queryModeDropdown).text().trim(), 'Advanced', 'The query mode dropdown is Advanced');
  assert.notOk(find(selectors.ruleBuilder), 'The Rule Builder is not present');
  assert.ok(find(selectors.advancedQuery), 'The Advanced Query textarea is present');
});

test('Changing the query mode displays a confirmation dialog. Canceling it keeps the Rule Builder query mode', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.equal(this.$(selectors.queryModeDropdown).text().trim(), 'Rule Builder', 'The query mode dropdown is Rule Builder');
  clickTrigger(selectors.queryMode);
  selectChoose(selectors.queryMode, 'Advanced');
  return wait().then(() => {
    assert.equal($(`#modalDestination ${selectors.queryModeConfirmationModal}`).length, 1, 'The confirmation modal appears');
    click(`${selectors.queryModeConfirmationModal} .cancel-button .rsa-form-button`);
    return wait().then(() => {
      assert.equal($(`#modalDestination ${selectors.queryModeConfirmationModal}`).length, 0, 'The confirmation modal is gone');
      assert.ok(find(selectors.ruleBuilder), 'The Rule Builder is present');
    });
  });
});

test('Confirming the query mode change dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  clickTrigger(selectors.queryMode);
  selectChoose(selectors.queryMode, 'Advanced');
  return wait().then(() => {
    assert.equal($(`#modalDestination ${selectors.queryModeConfirmationModal}`).length, 1, 'The confirmation modal appears');
    $(`${selectors.queryModeConfirmationModal} .confirm-button .rsa-form-button`).click();
    return wait().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
      actionSpy.restore();
    });
  });
});

test('Clicking the action radio button dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  click(selectors.suppressAlertActionInput);
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });
});

test('Changing the name dispatches the updateRule creator', function(assert) {
  spyRuleUpdate({ assert, test: this, selector: selectors.nameInput });
});

test('Changing the description dispatches the updateRule creator', function(assert) {
  spyRuleUpdate({ assert, test: this, selector: selectors.descriptionInput });
});

test('Changing the advanced query dispatches the updateRule creator', function(assert) {
  const state = { ...initialState, ruleInfo: { ...initialState.ruleInfo, advancedUiFilterConditions: true } };
  spyRuleUpdate({ assert, test: this, selector: selectors.advancedQuery, state });
});

test('Error message for name does not appear if the field has not been visited', function(assert) {
  setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, name: '' } });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.notOk(find('.rule-control.name .input-error'), 'There is no error message if the name has not been visited');
});

test('Error message for name does appear if the field has been visited', function(assert) {
  setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, name: '' }, visited: ['ruleInfo.name'] });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.ok(find('.rule-control.name .input-error'), 'There is an error message if the name has been visited');
});

test('Overlay appears if a transaction is underway', function(assert) {
  setState({ ...initialState, isTransactionUnderway: true });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.ok(find(selectors.transactionOverlay), 'There is a transaction overlay if isTransactionUnderway is true');
});

test('Footer Save Button is disabled and warning message is displayed when missing information', function(assert) {
  const translation = getOwner(this).lookup('service:i18n');
  setState({ ...initialState, ruleInfo: { ...initialState.ruleInfo, name: '' } });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  const warningMessage = translation.t('configure.incidentRules.missingRequiredInfo');
  assert.equal(this.$(selectors.formWarning).text().trim(), warningMessage, 'There is a missing information warning displayed in the footer');
  assert.equal(this.$(selectors.saveButton).is(':disabled'), true, 'The Save button is disabled');
});

test('Footer Save Button is disabled and warning message is displayed when user lacks permission to edit page', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule ruleId='12345'}}`);
  assert.equal(this.$(selectors.saveButton).is(':disabled'), false, 'The Save button is not disabled');
  const translation = getOwner(this).lookup('service:i18n');
  this.set('accessControl.roles', []);
  assert.equal(this.$(selectors.saveButton).is(':disabled'), true, 'The Save button is disabled');
  const warningMessage = translation.t('configure.incidentRules.noManagePermissions');
  assert.equal(this.$(selectors.formWarning).text().trim(), warningMessage, 'A warning is displayed to users that they have no permissions to edit');
});