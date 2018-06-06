import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';


let setState;

module('Integration | Component | deploy-log-parser', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Deploy button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers/toolbar}}`);
    assert.ok(find('.deploy-log-parser'), 'Deploy button is not showing');
  });

  test('Deploy confirmation', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers/toolbar}}`);
    click('.deploy-log-parser button');
    return settled().then(() => {
      assert.ok(find('.deploy-log-parser .confirmation-modal'), 'Modal Confirmation is not showing');
      assert.equal(find('.deploy-log-parser .confirmation-modal .modal-content p').textContent.trim(), 'Deploy rules for log parser \'builtin\' to all Log Decoders?', 'Confirm message is incorrect');
    });
  });

});