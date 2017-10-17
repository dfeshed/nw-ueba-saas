import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import rule from '../../../../data/subscriptions/aggregation-rules/queryRecord/data';
import fields from '../../../../data/subscriptions/aggregation-fields/findAll/data';
import * as aggregationRuleCreators from 'respond/actions/creators/aggregation-rule-creators';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';

const initialState = {
  rule,
  ruleStatus: 'complete',
  conditionGroups: { '0': { id: 0, logicalOperator: 'and' } },
  conditions: {},
  fields,
  fieldsStatus: 'complete'
};

let setState;

moduleForComponent('rule-builder/condition-group', 'Integration | Component | Respond Rule Builder Condition Group', {
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
  const conditionGroups = {
    '0': { id: 0 },
    '1': { id: 1, groupId: 0 },
    '2': { id: 2, groupId: 0 }
  };

  const conditions = {
    '0': { id: 0, groupId: 2 },
    '1': { id: 1, groupId: 1 }
  };

  setState({
    ...initialState,
    conditionGroups,
    conditions
  });
  this.set('groupInfo', conditionGroups['0']);
  this.render(hbs`{{rule-builder/condition-group info=groupInfo}}`);
  assert.equal(this.$('.rsa-rule-condition-group').length, 3, 'Based on state, there are three instances of condition-group in the DOM');
  assert.equal(this.$('.rsa-rule-condition').length, 2, 'Based on state, there are two conditions in the DOM');
});

test('The root group has no remove button', function(assert) {
  // const conditionGroups = { '0': { id: 0 } };
  setState({
    ...initialState
  });
  this.set('groupInfo', initialState.conditionGroups['0']);
  this.render(hbs`{{rule-builder/condition-group info=groupInfo isRootGroup=true}}`);
  assert.equal(this.$('.remove-group button').length, 0, 'There is no remove button on a root group');
});


test('Clicking the Add Condition button dispatches the addCondition action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'addCondition');
  setState({
    ...initialState
  });
  this.set('groupInfo', initialState.conditionGroups['0']);
  this.render(hbs`{{rule-builder/condition-group info=groupInfo}}`);
  this.$('.add-condition button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The addCondition action was called once');
  });
});

test('Clicking the Remove Group button dispatches the removeGroup action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'removeGroup');
  setState({
    ...initialState
  });
  this.set('groupInfo', initialState.conditionGroups['0']);
  this.render(hbs`{{rule-builder/condition-group info=groupInfo}}`);
  this.$('.remove-group button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The removeGroup action was called once');
    assert.ok(actionSpy.calledWith(0));
  });
});

test('Changing the group operator dispatches the upgradeGroup action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'updateGroup');
  setState({
    ...initialState
  });
  this.set('groupInfo', initialState.conditionGroups['0']);
  this.render(hbs`{{rule-builder/condition-group info=groupInfo}}`);
  clickTrigger('.group-operator');
  selectChoose('.group-operator', 'None of these');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateGroup action was called once');
  });
});