import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import rule from '../../../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../../../data/subscriptions/incident-fields/findAll/data';
import { clickTrigger, selectChoose } from '../../../../../../helpers/ember-power-select';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as incidentRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';
import sinon from 'sinon';

const initialState = {
  rule,
  ruleStatus: 'complete',
  conditionGroups: { '0': { id: 0, logicalOperator: 'and' } },
  conditions: {},
  fields,
  fieldsStatus: 'complete'
};

let setState;

module('Integration | Component | Respond Rule Builder Condition Group', function(hooks) {
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
    await render(hbs`{{respond/incident-rule/rule-builder/condition-group info=groupInfo}}`);
    assert.equal(findAll('.rsa-rule-condition-group').length, 3, 'Based on state, there are three instances of condition-group in the DOM');
    assert.equal(findAll('.rsa-rule-condition').length, 2, 'Based on state, there are two conditions in the DOM');
  });

  test('The root group has no remove button', async function(assert) {
    setState({
      ...initialState
    });
    this.set('groupInfo', initialState.conditionGroups['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition-group info=groupInfo isRootGroup=true}}`);
    assert.equal(findAll('.remove-group button').length, 0, 'There is no remove button on a root group');
  });


  test('Clicking the Add Condition button dispatches the addCondition action creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'addCondition');
    setState({
      ...initialState
    });
    this.set('groupInfo', initialState.conditionGroups['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition-group info=groupInfo}}`);
    await click('.add-condition button');
    assert.ok(actionSpy.calledOnce, 'The addCondition action was called once');
    actionSpy.restore();
  });

  test('Clicking the Remove Group button dispatches the removeGroup action creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'removeGroup');
    setState({
      ...initialState
    });
    this.set('groupInfo', initialState.conditionGroups['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition-group info=groupInfo}}`);
    await click('.remove-group button');
    assert.ok(actionSpy.calledOnce, 'The removeGroup action was called once');
    assert.ok(actionSpy.calledWith(0));
    actionSpy.restore();
  });

  test('Changing the group operator dispatches the upgradeGroup action creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'updateGroup');
    setState({
      ...initialState
    });
    this.set('groupInfo', initialState.conditionGroups['0']);
    await render(hbs`{{respond/incident-rule/rule-builder/condition-group info=groupInfo}}`);
    clickTrigger('.group-operator');
    selectChoose('.group-operator', 'None of these');
    assert.ok(actionSpy.calledOnce, 'The updateGroup action was called once');
    assert.equal(actionSpy.args[0][1].logicalOperator, 'nor', 'The logical operatator for "none of thse" is "nor"');
    actionSpy.restore();
  });
});

