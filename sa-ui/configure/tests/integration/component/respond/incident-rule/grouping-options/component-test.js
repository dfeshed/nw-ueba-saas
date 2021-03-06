import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { blur, click, fillIn, find, render } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import sinon from 'sinon';
import ruleInfo from '../../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../../data/subscriptions/incident-fields/findAll/data';
import categoryTags from '../../../../../data/subscriptions/category-tags/findAll/data';
import enabledUsers from '../../../../../data/subscriptions/users/findAll/data';
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

const spyRuleUpdate = async function({ assert, selector, value = 'To Rule is Divine' }) {
  assert.expect(1);
  const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
  setState({ ...initialState });
  await render(hbs`{{respond/incident-rule/grouping-options }}`);
  await fillIn(selector, value);
  await blur(selector);
  assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
  actionSpy.restore();
};

let setState;

module('Integration | Component | Respond Incident Rule Grouping Options', function(hooks) {
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
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    assert.ok(find(selectors.groupingOptions), 'The component appears in the DOM');
  });

  test('The form displays the expected data in the form fields', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    assert.ok(find(selectors.groupByDropdown), 'The group by fields select has one options selected');
    assert.ok(find(selectors.groupByDropdown).textContent.indexOf('Alert Name') >= 0, 'The selected option is Alert Name');
    assert.equal(find(selectors.timeWindowValueInput).value, '7', 'The time window value is 7 from the data duration "7d"');
    assert.equal(find(selectors.timeWindowUnitDropdown).textContent.trim(), 'Days', 'The time window unit is "Days" from the data duration "7d"');
    assert.equal(find(selectors.incidentTitleInput).value.trim(), '${ruleName} for ${groupByValue1}', 'The incident title appears as expected');
    assert.equal(find(selectors.incidentSummaryTextarea).value.trim(), 'Summary', 'The incident summary appears as expected');
    assert.ok(find(selectors.incidentCategories), 'The incident categories select has one options selected');
    assert.ok(find(selectors.incidentCategories).textContent.indexOf('Environmental: Deterioration') >= 0, 'The selected category option is "Environment: Deterioaration"');
    assert.equal(find(selectors.incidentAssignee).textContent.trim(), 'Stanley Nellie', 'The incident assignee appears as expected');
    assert.ok(find(selectors.averageScoringOptionInput).checked, 'The "average" scoring option is checked');
    assert.equal(find(selectors.criticalScoreBeginRange).value, '95', 'The critical score begin range is as expeccted');
    assert.equal(find(selectors.highScoreBeginRange).value, '55', 'The high score begin range is as expeccted');
    assert.equal(find(selectors.mediumScoreBeginRange).value, '25', 'The medium score begin range is as expeccted');
    assert.equal(find(selectors.lowScoreBeginRange).value, '5', 'The low score begin range is as expeccted');
  });

  test('Changing the group-by fields selection dispatches the updateRule creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    await selectChoose('.rule-control.group-by-fields .ember-power-select-trigger', 'Alert Type');
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Changing the time window value dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.timeWindowValueInput, value: '10' });
  });

  test('Changing the time window unit dispatches the updateRule creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    await selectChoose(selectors.timeWindowUnitDropdown, 'Minutes');
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Changing the incident title dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.incidentTitleInput });
  });

  test('Changing the incident summary dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.incidentSummaryTextarea });
  });

  test('Changing the scoring range critical dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.criticalScoreBeginRange, value: '90' });
  });

  test('Changing the scoring range high dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.highScoreBeginRange, value: '50' });
  });

  test('Changing the scoring range medium dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.mediumScoreBeginRange, value: '20' });
  });

  test('Changing the scoring range low dispatches the updateRule creator', async function(assert) {
    return spyRuleUpdate.bind(this)({ assert, selector: selectors.lowScoreBeginRange, value: '2' });
  });

  test('Changing the categories selection dispatches the updateRule creator', async function(assert) {
    assert.expect(1);
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    await selectChoose('.rule-control.incident-categories .ember-power-select-trigger', 'Hacking: XSS');
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Changing the assignee selection dispatches the updateRule creator', async function(assert) {
    assert.expect(1);
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    await selectChoose(selectors.incidentAssignee, 'Sim Boyd');
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Clicking the high score radio button dispatches the updateRule creator', async function(assert) {
    assert.expect(1);
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    await click(selectors.highScoringOptionInput);
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });

  test('Clicking the count score radio button dispatches the updateRule creator', async function(assert) {
    assert.expect(1);
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateRule');
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/grouping-options }}`);
    await click(selectors.countScoringOptionInput);
    assert.ok(actionSpy.calledOnce, 'The updateRule creator was called once');
    actionSpy.restore();
  });
});