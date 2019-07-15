import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled, findAll, fillIn } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | delete-rules', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });


  test('Delete button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.ok(find('.buttons-container .deleteRule'), 'Delete button is not showing');
  });

  test('Delete a rule confirmation', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).parserRulesSaveWait(false).build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{content/log-parser-rules/parser-rules-list/buttons-container}}
    `);
    click('.buttons-container .deleteRule button');
    return settled().then(() => {
      assert.ok(find('.buttons-container .deleteRule .confirmation-modal'), 'Modal Confirmation is not showing');
      assert.equal(find('.buttons-container .deleteRule .confirmation-modal .modal-content p').textContent.trim(), 'Delete rule \'ipv4\' from this log parser?', 'Confirm message is incorrect');
    });
  });

  test('Try to delete a non ootb rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.notOk(find('.buttons-container .deleteRule .is-disabled'), 'Delete button is disabled');
  });
  test('Try to delete a ootb rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.ok(find('.buttons-container .deleteRule .is-disabled'), 'Delete button is not disabled');
  });

  test('Add a new rule modal', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{content/log-parser-rules/parser-rules-list/buttons-container}}
    `);
    assert.ok(find('.buttons-container .add-new-parser-rule .modal-trigger'), 'Add New button is not showing');
    await click('.buttons-container .add-new-parser-rule .modal-trigger');
    assert.equal(findAll('#modalDestination .add-new-parser-rule button').length, 1, 'Modal is not showing');
    await fillIn('#modalDestination .add-new-parser-rule .ember-text-field.ember-view', '123');
    assert.equal(find('#modalDestination .add-new-parser-rule input').value, '123', 'Name of rule is 123');
  });
});