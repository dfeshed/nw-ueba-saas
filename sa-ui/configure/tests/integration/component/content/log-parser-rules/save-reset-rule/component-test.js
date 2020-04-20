import { module, test } from 'qunit';
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

  test('Save button shows and is disabled when there are no', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.equal(find('.save-reset-rule .saveRule button').disabled, true, 'Save button is not showing');
  });

  test('Save button shows and is enabled when there are changes', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1).hasChanges().build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.equal(find('.saveRule button').disabled, false, 'Save button is not showing');
  });

  test('The Save button is disabled if the user does not have manage permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    new ReduxDataHelper(setState).parserRulesFormatData(1).build();
    accessControl.set('roles', ['content-server.logparser.read']);
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.equal(find('.saveRule button').disabled, true, 'Deploy button is not disabled');
  });

  test('Discard Changes button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.ok(find('.save-reset-rule .resetRule button'), 'Reset button is not showing');
  });

  test('Deploy confirmation', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('configure.logsParser.modals.deployLogParser.confirm', { logParser: 'builtin' });
    new ReduxDataHelper(setState).formatOptions().parserRulesFormatData(0).build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{content/log-parser-rules/save-reset-rule}}
    `);
    assert.ok(find('.deploy-log-parser'), 'Deploy button is not showing');
    await click('.deploy-log-parser button');
    assert.ok(find('.deploy-log-parser .confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.deploy-log-parser .confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
  });

  test('The Deploy button is disabled if the user does not have manage permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    new ReduxDataHelper(setState).formatOptions().parserRulesFormatData(0).build();
    accessControl.set('roles', ['content-server.logparser.read']);
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.equal(find('.deploy-log-parser button').disabled, true, 'Deploy button is not disabled');
  });
});