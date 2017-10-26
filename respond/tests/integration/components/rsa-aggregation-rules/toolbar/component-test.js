import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import wait from 'ember-test-helpers/wait';
import rules from '../../../../data/subscriptions/aggregation-rules/findAll/data';
import * as alertRuleCreators from 'respond/actions/creators/aggregation-rule-creators';

const initialState = {
  rules,
  rulesStatus: 'complete',
  isTransactionUnderway: false,
  selectedRule: null
};

let setState;

moduleForComponent('rsa-aggregation-rules/toolbar', 'Integration | Component | Respond Aggregation Rules Toolbar', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { respond: { aggregationRules: state } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('The component appears in the DOM with the expected number of rows', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{rsa-aggregation-rules/toolbar}}`);
  assert.equal(this.$('.aggregation-rules-toolbar').length, 1, 'The component appears in the DOM');
  assert.equal(this.$('.clone-rule-button .rsa-form-button-wrapper.is-disabled').length, 1, 'The clone button is disabled');
  assert.equal(this.$('.delete-rule-button .rsa-form-button-wrapper.is-disabled').length, 1, 'The delete button is disabled');
});

test('The clone and delete buttons are enabled if a rule is selected', function(assert) {
  setState({ ...initialState, selectedRule: '59b92bbf4cb0f0092b6b6a8b' });
  this.render(hbs`{{rsa-aggregation-rules/toolbar}}`);
  assert.equal(this.$('.clone-rule-button .rsa-form-button-wrapper.is-disabled').length, 0, 'The clone button is disabled');
  assert.equal(this.$('.delete-rule-button .rsa-form-button-wrapper.is-disabled').length, 0, 'The delete button is disabled');
});

test('Clicking on delete button dispatches deleteRule creator', function(assert) {
  const actionSpy = sinon.spy(alertRuleCreators, 'deleteRule');
  setState({ ...initialState, selectedRule: '59b92bbf4cb0f0092b6b6a8b' });
  this.render(hbs`{{rsa-aggregation-rules/toolbar}}`);
  this.$('.delete-rule-button button').click();
  return wait().then(() => {
    const confirmDialogOkButton = this.$('.respond-confirmation-dialog .confirm-button .rsa-form-button');
    assert.equal(confirmDialogOkButton.length, 1, 'The confirmation dialog appears');
    confirmDialogOkButton.click();
    return wait().then(() => {
      assert.ok(actionSpy.calledOnce, 'The deleteRule creator was called once');
      actionSpy.restore();
    });
  });
});
