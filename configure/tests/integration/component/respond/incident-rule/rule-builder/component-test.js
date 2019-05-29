import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import rule from '../../../../../data/subscriptions/incident-rules/queryRecord/data';
import fields from '../../../../../data/subscriptions/incident-fields/findAll/data';
import ruleNormalizer from 'configure/reducers/respond/incident-rules/incident-rule-normalizer';
import * as incidentRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';
import sinon from 'sinon';


const ruleConditions = JSON.parse(rule.uiFilterConditions);
const { groups, conditions } = ruleNormalizer.processRuleConfiguration(ruleConditions);
const initialState = {
  rule,
  ruleStatus: null,
  conditionGroups: groups,
  conditions,
  fields,
  fieldsStatus: null
};

let setState;

module('Integration | Component | Respond Incident Rule Builder', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { configure: { respond: { incidentRule: state } } };
      applyPatch(Immutable.from(fullState));
      this.redux = this.owner.lookup('service:redux');
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rule/rule-builder}}`);
    assert.equal(this.$('.rsa-rule-builder').length, 1, 'The component appears in the DOM');
  });

  test('Clicking the Add Group button dispatches the addGroup action creator', async function(assert) {
    const actionSpy = sinon.spy(incidentRuleCreators, 'addGroup');
    setState({
      ...initialState
    });
    await render(hbs`{{respond/incident-rule/rule-builder}}`);
    this.$('.rule-builder-toolbar button').click();
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The addGroup action was called once');
    });
  });
});