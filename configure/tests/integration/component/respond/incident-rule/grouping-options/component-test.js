import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';
import ruleInfo from '../../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../../data/subscriptions/incident-fields/findAll/data';
import categoryTags from '../../../../../data/subscriptions/category-tags/findAll/data';
import enabledUsers from '../../../../../data/subscriptions/users/findAll/data';
import { clickTrigger, selectChoose } from '../../../../../helpers/ember-power-select';
import {
  click,
  fillIn,
  find,
  findAll,
  triggerEvent
} from 'ember-native-dom-helpers';
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
  groupingOptions: '.incident-rule-grouping-options',
  groupByDropdown: '.rule-control.group-by-fields .ember-power-select-trigger .ember-power-select-multiple-option',
  timeWindowValueInput: '.rule-control.time-window input',
  timeWindowUnitDropdown: '.rule-control.time-window .ember-power-select-trigger',
  incidentTitleInput: '.rule-control.incident-title input',
  incidentSummaryTextarea: '.rule-control.incident-summary textarea',
  incidentCategories: '.rule-control.incident-categories .ember-power-select-trigger .ember-power-select-multiple-option',
  incidentAssignee: '.rule-control.incident-assignee .ember-power-select-trigger',
  averageScoringOptionInput: '.rule-control.priority .scoring-options input[value="average"]',
  highScoringOptionInput: '.rule-control.priority .scoring-options input[value="high"]',
  countScoringOptionInput: '.rule-control.priority .scoring-options input[value="count"]',
  criticalScoreBeginRange: '.rule-control.priority .scoring-ranges input.critical',
  highScoreBeginRange: '.rule-control.priority .scoring-ranges input.high',
  mediumScoreBeginRange: '.rule-control.priority .scoring-ranges input.medium',
  lowScoreBeginRange: '.rule-control.priority .scoring-ranges input.low'
};

moduleForComponent('respond/incident-rule/grouping-options', 'Integration | Component | Respond Incident Rule Grouping Options', {
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
  },
  afterEach() {
    revertPatch();
  }
});

const spyRuleUpdate = function({ assert, selector, value = 'To Rule is Divine' }) {
  assert.expect(1);
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  fillIn(selector, value);
  triggerEvent(selector, 'blur');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });
};

test('The component appears in the DOM', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  assert.ok(find(selectors.groupingOptions), 'The component appears in the DOM');
});

test('The form displays the expected data in the form fields', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  assert.equal(findAll(selectors.groupByDropdown).length, 1, 'The group by fields select has one options selected');
  assert.ok($(findAll(selectors.groupByDropdown)[0]).text().indexOf('Alert Name') >= 0, 'The selected option is Alert Name');
  assert.equal(this.$(selectors.timeWindowValueInput).val(), '7', 'The time window value is 7 from the data duration "7d"');
  assert.equal(this.$(selectors.timeWindowUnitDropdown).text().trim(), 'Days', 'The time window unit is "Days" from the data duration "7d"');
  assert.equal(this.$(selectors.incidentTitleInput).val().trim(), '${ruleName} for ${groupByValue1}', 'The incident title appears as expected');
  assert.equal(this.$(selectors.incidentSummaryTextarea).val().trim(), 'Summary', 'The incident summary appears as expected');
  assert.equal(findAll(selectors.incidentCategories).length, 1, 'The incident categories select has one options selected');
  assert.ok($(findAll(selectors.incidentCategories)[0]).text().indexOf('Environmental: Deterioration') >= 0, 'The selected category option is "Environment: Deterioaration"');
  assert.equal(this.$(selectors.incidentAssignee).text().trim(), 'Stanley Nellie', 'The incident assignee appears as expected');
  assert.equal(this.$(selectors.averageScoringOptionInput).is(':checked'), true, 'The "average" scoring option is checked');
  assert.equal(this.$(selectors.criticalScoreBeginRange).val(), '95', 'The critical score begin range is as expeccted');
  assert.equal(this.$(selectors.highScoreBeginRange).val(), '55', 'The high score begin range is as expeccted');
  assert.equal(this.$(selectors.mediumScoreBeginRange).val(), '25', 'The medium score begin range is as expeccted');
  assert.equal(this.$(selectors.lowScoreBeginRange).val(), '5', 'The low score begin range is as expeccted');
});

test('Changing the group-by fields selection dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  clickTrigger('.rule-control.group-by-fields');
  selectChoose('.rule-control.group-by-fields .ember-power-select-trigger', 'Alert Type');
  assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
  actionSpy.restore();
});

test('Changing the time window value dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.timeWindowValueInput, value: '10' });
});

test('Changing the time window unit dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  clickTrigger('.rule-control.time-window');
  selectChoose(selectors.timeWindowUnitDropdown, 'Minutes');
  assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
  actionSpy.restore();
});

test('Changing the incident title dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.incidentTitleInput });
});

test('Changing the incident summary dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.incidentSummaryTextarea });
});

test('Changing the scoring range critical dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.criticalScoreBeginRange, value: '90' });
});

test('Changing the scoring range high dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.highScoreBeginRange, value: '50' });
});

test('Changing the scoring range medium dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.mediumScoreBeginRange, value: '20' });
});

test('Changing the scoring range low dispatches the updateRule creator', function(assert) {
  return spyRuleUpdate.bind(this)({ assert, selector: selectors.lowScoreBeginRange, value: '2' });
});

test('Changing the categories selection dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  clickTrigger('.rule-control.incident-categories');
  selectChoose('.rule-control.incident-categories .ember-power-select-trigger', 'Hacking: XSS');
  assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
  actionSpy.restore();
});

test('Changing the assignee selection dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  clickTrigger('.rule-control.incident-assignee');
  selectChoose(selectors.incidentAssignee, 'Sim Boyd');
  assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
  actionSpy.restore();
});

test('Clicking the high score radio button dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  click(selectors.highScoringOptionInput);
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });
});

test('Clicking the count score radio button dispatches the updateRule creator', function(assert) {
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rule/grouping-options }}`);
  click(selectors.countScoringOptionInput);
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });
});
