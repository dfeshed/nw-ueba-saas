import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled } from '@ember/test-helpers';
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
    assert.ok(find('.buttonsContainer .deleteRule'), 'Delete button is not showing');
  });

  test('Delete button does not shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(-1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.notOk(find('.buttonsContainer .deleteRule'), 'Delete button is showing');
  });

  test('Delete a rule confirmation', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    click('.buttonsContainer .deleteRule button');
    return settled().then(() => {
      assert.ok(find('.buttonsContainer .deleteRule .confirmation-modal'), 'Modal Confirmation is not showing');
      assert.equal(find('.buttonsContainer .deleteRule .confirmation-modal .modal-content p').textContent.trim(), 'Click OK to delete Rule \'ipv4\'', 'Confirm message is incorrect');
    });
  });

  test('Try to delete a non ootb rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.notOk(find('.buttonsContainer .deleteRule .is-disabled'), 'Delete button is disabled');
  });
  test('Try to delete a ootb rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/buttons-container}}`);
    assert.ok(find('.buttonsContainer .deleteRule .is-disabled'), 'Delete button is not disabled');
  });

});