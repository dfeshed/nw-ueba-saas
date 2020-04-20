import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { waitForRedux } from '../../../../../helpers/wait-for-redux';

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

  test('Log Parsers will render infotext with no parsers', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{content/log-parser-rules/log-parsers domIsReady=true}}`);
    assert.notOk(find('.log-parsers .firstItem'), 'First item is not showing with no parsers loaded');
    assert.ok(find('.log-parsers .no-loaded-parsers'), 'InfoText is showing with no parsers loaded');
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
    assert.expect(1);
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers}}`);
    await click('.log-parsers .firstItem');
    await waitForRedux('configure.content.logParserRules.sampleLogsStatus', 'completed');
    assert.ok(find('.log-parsers .active'), 'The log parser was not selected');
  });

});
