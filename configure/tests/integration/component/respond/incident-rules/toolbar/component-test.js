import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { waitUntil, click, find, render } from '@ember/test-helpers';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../helpers/patch-flash';
import rules from '../../../../../data/subscriptions/incident-rules/findAll/data';

const initialState = {
  rules,
  rulesStatus: 'complete',
  isTransactionUnderway: false,
  selectedRules: []
};

let setState;

module('Integration | Component | Respond Incident Rules Toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      const fullState = { configure: { respond: { incidentRules: state } } };
      patchReducer(this, fullState);
    };
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/incident-rules/toolbar}}`);
    assert.ok(find('.incident-rules-toolbar'), 'The component appears in the DOM');
    assert.ok(find('.clone-rule-button .rsa-form-button-wrapper.is-disabled'), 'The clone button is disabled');
    assert.ok(find('.delete-rule-button .rsa-form-button-wrapper.is-disabled'), 'The delete button is disabled');
  });

  test('The clone and delete buttons are enabled if a rule is selected', async function(assert) {
    setState({ ...initialState, selectedRules: ['59b92bbf4cb0f0092b6b6a8b'] });
    await render(hbs`{{respond/incident-rules/toolbar}}`);
    assert.notOk(find('.clone-rule-button .rsa-form-button-wrapper.is-disabled'), 'The clone button is disabled');
    assert.notOk(find('.delete-rule-button .rsa-form-button-wrapper.is-disabled'), 'The delete button is disabled');
  });

  test('The clone and delete buttons are disabled if multiple rules are selected', async function(assert) {
    setState({ ...initialState, selectedRules: ['59b92bbf4cb0f0092b6b6a8b', '987654321'] });
    await render(hbs`{{respond/incident-rules/toolbar}}`);
    assert.ok(find('.clone-rule-button .rsa-form-button-wrapper.is-disabled'), 'The clone button is disabled');
    assert.ok(find('.delete-rule-button .rsa-form-button-wrapper.is-disabled'), 'The delete button is disabled');
  });

  test('Clicking on delete button dispatches deleteRule creator', async function(assert) {
    assert.expect(4);

    setState({ ...initialState, selectedRules: ['59b92bbf4cb0f0092b6b6a8b'] });
    await render(hbs`{{respond/incident-rules/toolbar}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'deleteRecord');
      assert.equal(modelName, 'incident-rules');
      assert.deepEqual(query, {
        data: {
          id: '59b92bbf4cb0f0092b6b6a8b'
        }
      });
    });

    await click('.delete-rule-button button');
    const confirmDialogOkButton = find('.respond-confirmation-dialog .confirm-button .rsa-form-button');
    assert.ok(confirmDialogOkButton, 'The confirmation dialog appears');
    confirmDialogOkButton.click();
  });

  test('Clicking on clone button dispatches cloneRule creator', async function(assert) {
    assert.expect(5);

    let clonedRuleId;
    this.set('transitionToRule', (ruleId) => {
      clonedRuleId = ruleId;
    });

    setState({ ...initialState, selectedRules: ['59b92bbf4cb0f0092b6b6a8b'] });
    await render(hbs`{{respond/incident-rules/toolbar transitionToRule=(action transitionToRule)}}`);

    patchFlash((flash) => {
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, 'You successfully cloned the selected rule');
    });

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'createRecord');
      assert.equal(modelName, 'incident-rule-clone');
      assert.deepEqual(query, {
        data: {
          id: '59b92bbf4cb0f0092b6b6a8b'
        }
      });
    });

    await click('.clone-rule-button button');
    await waitUntil(() => clonedRuleId !== undefined, { timeout: 15000 });
  });
});
