import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, findAll, render, fillIn, triggerEvent } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ruleInfo from '../../../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../../../data/subscriptions/incident-fields/findAll/data';
import * as aggregationRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';
import sinon from 'sinon';
import { clickTrigger, selectChoose } from '../../../../../../helpers/ember-power-select';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const initialState = {
  ruleInfo,
  ruleStatus: 'complete',
  conditionGroups: {},
  conditions: { '0': { id: 0, property: 'alert.type', operator: '=' } },
  fields,
  fieldsStatus: 'complete'
};

let setState;

module('Integration | Component | Respond Rule Builder Condition', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { configure: { respond: { incidentRule: state } } };
      applyPatch(Immutable.from(fullState));
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/rule-builder/condition}}`);
    assert.equal(findAll('.rsa-rule-condition').length, 1, 'The component appears in the DOM');
  });

  test('Clicking on the close button dispatches the removeCondition action creator', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'removeCondition');
    const conditions = { '0': { id: 0 } };
    setState({
      ...initialState,
      conditions
    });
    this.set('conditionInfo', conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);
    await click('.delete-condition button');
    assert.ok(actionSpy.calledOnce, 'The removeCondition action was called once');
    actionSpy.restore();
  });

  test('Changing the field dispatches an updateCondition action creator', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
    const conditions = { '0': { id: 0 } };
    setState({
      ...initialState,
      conditions
    });
    this.set('conditionInfo', conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);
    clickTrigger('.field');
    selectChoose('.field', 'Alert Type');
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    assert.equal(actionSpy.args[0][0], 0, 'The first argument is the condition id of zero');
    assert.deepEqual(actionSpy.args[0][1], { property: 'alert.type', operator: '=', value: null },
      'The second argument is an object with the alert type property, a default operator, and a null value');
    actionSpy.restore();
  });

  test('Changing the operator dispatches an updateCondition action creator', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
    const conditions = { '0': { id: 0, property: 'alert.type' } };
    setState({
      ...initialState,
      conditions
    });
    this.set('conditionInfo', conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);
    clickTrigger('.operator');
    selectChoose('.operator', 'is equal to');
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });

  test('Changing the category value dispatches an updateCondition action creator', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
    setState({
      ...initialState
    });
    this.set('conditionInfo', initialState.conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);
    clickTrigger('.value');
    selectChoose('.value', 'Manual Upload');
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });

  test('Changing the text value dispatches an updateCondition action creator', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
    const conditions = { '0': { id: 0, property: 'alert.name', operator: '=' } };
    setState({
      ...initialState,
      conditions
    });
    this.set('conditionInfo', conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);
    const inputSelector = '.condition-control.value input[type=text]';
    assert.equal(findAll(inputSelector).length, 1, 'There is a text input for a text based field');
    await fillIn(inputSelector, 'I think we need a bigger boat');
    await triggerEvent(inputSelector, 'blur');
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });

  test('A numberfield type is converted to a number', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
    const conditions = { '0': { id: 0, property: 'alert.risk_score', operator: '=' } };
    const inputSelector = '.condition-control.value input[type=number]';
    setState({
      ...initialState,
      conditions
    });
    this.set('conditionInfo', conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);

    await fillIn(inputSelector, '80');
    await triggerEvent(inputSelector, 'blur');
    assert.deepEqual(actionSpy.args[0][1], { value: 80 },
      'The second argument is an object with the value as an integer (not a string)');
    actionSpy.restore();
  });

  test('An empty field for a numberfield condition value results in a null value on update', async function(assert) {
    const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
    const conditions = { '0': { id: 0, property: 'alert.risk_score', operator: '=' } };
    const inputSelector = '.condition-control.value input[type=number]';
    setState({
      ...initialState,
      conditions
    });
    this.set('conditionInfo', conditions['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition info=conditionInfo}}`);

    await fillIn(inputSelector, '    ');
    await triggerEvent(inputSelector, 'blur');
    assert.deepEqual(actionSpy.args[0][1], { value: null },
      'An empty string results in a null value in the arguments');
    actionSpy.restore();
  });
});

