import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { click, find, render } from '@ember/test-helpers';
import sinon from 'sinon';
import rules from '../../../../../data/subscriptions/incident-rules/findAll/data';
import * as alertRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';

const initialState = {
  rules,
  rulesStatus: 'complete',
  isTransactionUnderway: false,
  selectedRule: null
};

let setState;

module('Integration | Component | Respond Incident Rules Toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { configure: { respond: { incidentRules: state } } };
      patchReducer(this, fullState);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rules/toolbar}}`);
    assert.ok(find('.incident-rules-toolbar'), 'The component appears in the DOM');
    assert.ok(find('.clone-rule-button .rsa-form-button-wrapper.is-disabled'), 'The clone button is disabled');
    assert.ok(find('.delete-rule-button .rsa-form-button-wrapper.is-disabled'), 'The delete button is disabled');
  });

  test('The clone and delete buttons are enabled if a rule is selected', async function(assert) {
    setState({ ...initialState, selectedRule: '59b92bbf4cb0f0092b6b6a8b' });
    await render(hbs`{{respond/incident-rules/toolbar}}`);
    assert.notOk(find('.clone-rule-button .rsa-form-button-wrapper.is-disabled'), 'The clone button is disabled');
    assert.notOk(find('.delete-rule-button .rsa-form-button-wrapper.is-disabled'), 'The delete button is disabled');
  });

  test('Clicking on delete button dispatches deleteRule creator', async function(assert) {
    const actionSpy = sinon.spy(alertRuleCreators, 'deleteRule');
    setState({ ...initialState, selectedRule: '59b92bbf4cb0f0092b6b6a8b' });
    await render(hbs`{{respond/incident-rules/toolbar}}`);
    await click('.delete-rule-button button');
    const confirmDialogOkButton = find('.respond-confirmation-dialog .confirm-button .rsa-form-button');
    assert.ok(confirmDialogOkButton, 'The confirmation dialog appears');
    confirmDialogOkButton.click();
    assert.ok(actionSpy.calledOnce, 'The deleteRule creator was called once');
    actionSpy.restore();
  });
});