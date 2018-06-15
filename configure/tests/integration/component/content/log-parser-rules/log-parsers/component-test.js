import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';


let setState;

module('Integration | Component | log parsers', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Log Parsers will render', async function(assert) {
    new ReduxDataHelper(setState).parserRulesSaveWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers}}`);
    assert.equal(find('.log-parsers .firstItem').textContent.trim(), 'builtin', 'Log Parsers did render');
  });

  test('Log Parsers will render dirty-deployed', async function(assert) {
    new ReduxDataHelper(setState).parserListState(true, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers}}`);
    assert.equal(find('.log-parsers .firstItem').textContent.trim(), 'ParserListState', 'Log Parsers did render');
  });

  // test('Log Parsers will render dirty-notdeployed', async function(assert) {
  //   new ReduxDataHelper(setState).parserRulesSaveWait(false).parserRulesFormatData(0, true).parserListState(true, false).build();
  //   await render(hbs`{{content/log-parser-rules/log-parsers}}`);
  //   assert.equal(find('.log-parsers .firstItem').textContent.trim(), 'builtin-', 'Log Parsers did render');
  // });

  // test('Log Parsers will render notdirty-deployed', async function(assert) {
  //   new ReduxDataHelper(setState).parserListState(false, true).build();
  //   await render(hbs`{{content/log-parser-rules/log-parsers}}`);
  //   assert.equal(find('.log-parsers .firstItem').textContent.trim(), 'builtin', 'Log Parsers did render');
  // });

  // test('Log Parsers will render notdirty-notdeployed', async function(assert) {
  //   new ReduxDataHelper(setState).parserListState(false, false).build();
  //   await render(hbs`{{content/log-parser-rules/log-parsers}}`);
  //   assert.equal(find('.log-parsers .firstItem').textContent.trim(), 'builtin-', 'Log Parsers did render');
  // });

  test('Select a log parser', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers}}`);
    await click('.log-parsers .firstItem');
    return settled().then(() => {
      assert.ok(find('.log-parsers .active'), 'The log parser was not selected');
    });
  });

});
