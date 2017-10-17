import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import rule from '../../../data/subscriptions/aggregation-rules/queryRecord/data';
import fields from '../../../data/subscriptions/aggregation-fields/findAll/data';
import { ruleNormalizer } from 'respond/reducers/respond/aggregation-rules/aggregation-rule';
import * as aggregationRuleCreators from 'respond/actions/creators/aggregation-rule-creators';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';


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

moduleForComponent('rule-builder', 'Integration | Component | Respond Rule Builder', {
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
  this.render(hbs`{{rule-builder}}`);
  assert.equal(this.$('.rsa-rule-builder').length, 1, 'The component appears in the DOM');
});

test('Clicking the Add Group button dispatches the addGroup action creator', function(assert) {
  const actionSpy = sinon.spy(aggregationRuleCreators, 'addGroup');
  setState({
    ...initialState
  });
  this.render(hbs`{{rule-builder}}`);
  this.$('.rule-builder-toolbar button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The addGroup action was called once');
  });
});