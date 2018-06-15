import { module, test, skip } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | save-reset-rule', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Save button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.ok(find('.save-reset-rule .saveRule button'), 'Save button is not showing');
  });

  test('Reset button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.ok(find('.save-reset-rule .resetRule button'), 'Reset button is not showing');
  });

  skip('Deploy confirmation', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('configure.logsParser.modals.deployLogParser.confirm', { logParser: 'builtin' });
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.ok(find('.deploy-log-parser'), 'Deploy button is not showing');
    await click('.deploy-log-parser button');
    assert.ok(find('.deploy-log-parser .confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.deploy-log-parser .confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
  });
});