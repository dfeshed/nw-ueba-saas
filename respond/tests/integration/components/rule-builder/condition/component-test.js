import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ruleInfo from '../../../../data/subscriptions/aggregation-rules/queryRecord/data';
import fields from '../../../../data/subscriptions/aggregation-fields/findAll/data';
import * as aggregationRuleCreators from 'respond/actions/creators/aggregation-rule-creators';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';

const initialState = {
  ruleInfo,
  ruleStatus: 'complete',
  conditionGroups: {},
  conditions: { '0': { id: 0, property: 'alert.type', operator: '=' } },
  fields,
  fieldsStatus: 'complete'
};

let setState;

moduleForComponent('rule-builder/condition', 'Integration | Component | Respond Rule Builder Condition', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { respond: { aggregationRule: state } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('The component appears in the DOM', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{rule-builder/condition}}`);
  assert.equal(this.$('.rsa-rule-condition').length, 1, 'The component appears in the DOM');
});

test('Clicking on the close button dispatches the removeCondition action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'removeCondition');
  const conditions = { '0': { id: 0 } };
  setState({
    ...initialState,
    conditions
  });
  this.set('conditionInfo', conditions['0']);
  this.render(hbs`{{rule-builder/condition info=conditionInfo}}`);
  this.$('.delete-condition button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The removeCondition action was called once');
    actionSpy.restore();
  });
});

test('Changing the field dispatches an updateCondition action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
  const conditions = { '0': { id: 0 } };
  setState({
    ...initialState,
    conditions
  });
  this.set('conditionInfo', conditions['0']);
  this.render(hbs`{{rule-builder/condition info=conditionInfo}}`);
  clickTrigger('.field');
  selectChoose('.field', 'Alert Type');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });
});

test('Changing the operator dispatches an updateCondition action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
  const conditions = { '0': { id: 0, property: 'alert.type' } };
  setState({
    ...initialState,
    conditions
  });
  this.set('conditionInfo', conditions['0']);
  this.render(hbs`{{rule-builder/condition info=conditionInfo}}`);
  clickTrigger('.operator');
  selectChoose('.operator', 'is equal to');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });
});

test('Changing the category value dispatches an updateCondition action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
  setState({
    ...initialState
  });
  this.set('conditionInfo', initialState.conditions['0']);
  this.render(hbs`{{rule-builder/condition info=conditionInfo}}`);
  clickTrigger('.value');
  selectChoose('.value', 'Manual Upload');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });
});

test('Changing the text value dispatches an updateCondition action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'updateCondition');
  const conditions = { '0': { id: 0, property: 'alert.name', operator: '=' } };
  setState({
    ...initialState,
    conditions
  });
  this.set('conditionInfo', conditions['0']);
  this.render(hbs`{{rule-builder/condition info=conditionInfo}}`);
  const inputSelector = '.condition-control.value input[type=text]';
  assert.equal(this.$(inputSelector).length, 1, 'There is a text input for a text based field');
  this.$(inputSelector).val('I think we need a bigger boat').blur();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateCondition action was called once');
    actionSpy.restore();
  });
});