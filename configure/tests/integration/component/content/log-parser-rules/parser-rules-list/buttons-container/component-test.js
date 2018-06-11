import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import $ from 'jquery';

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
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
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
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.ok(find('.buttons-container .addNewRule .modal-trigger'), 'Add New button is not showing');
    click('.buttons-container .addNewRule .modal-trigger');
    return settled().then(() => {
      assert.equal($('#modalDestination .addNewRule button').length, 1, 'Modal is not showing');
      $('#modalDestination .addNewRule .ember-text-field.ember-view').val('123');
      return settled().then(() => {
        assert.equal($('#modalDestination .addNewRule input').val(), '123', 'Name of rule is 123');
      });
    });
  });

});