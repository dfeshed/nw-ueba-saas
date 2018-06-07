import { module, test, skip } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';


let setState;

module('Integration | Component | rules-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Rules will wait', async function(assert) {
    new ReduxDataHelper(setState).parserRulesData(true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list}}`);
    assert.ok(find('.parser-rules-list .loading'), 'The rules spinner did not show');
  });

  test('Delete Rules will wait', async function(assert) {
    new ReduxDataHelper(setState).parserRulesDeleteWait(true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list}}`);
    assert.ok(find('.parser-rules-list .loading'), 'The rules spinner did not show when deleting');
  });

  test('Delete Rules is completed', async function(assert) {
    new ReduxDataHelper(setState).parserRulesDeleteWait(false).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list}}`);
    assert.notOk(find('.parser-rules-list .loading'), 'Delete rule not completed');
  });

  test('Rules will render', async function(assert) {
    new ReduxDataHelper(setState).parserRulesData(false).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list}}`);
    assert.equal(find('.parser-rules-list .firstItem').textContent.trim(), 'ipv4', 'Rules did render');
  });

  test('Select a rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list}}`);
    await click('.parser-rules-list .firstItem');
    return settled().then(() => {
      assert.ok(find('.parser-rules-list .active'), 'The rule was not selected');
    });
  });

  skip('Delete a rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list}}`);
    await click('.parser-rules-list .firstItem');
    await click('.parser-rules-list .deleteRule button');
    await click('.parser-rules-list .deleteRule .confirmation-modal .is-primary button');
    assert.notOk(find('.parser-rules-list .active'), 'The rule was not deleted');
  });

});
