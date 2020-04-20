import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled, triggerEvent } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { patchSocket, throwSocket } from '../../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../../helpers/patch-flash';

let setState;

module('Integration | Component | Parser Toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Expected Toolbar buttons are found', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers/toolbar}}`);
    assert.ok(find('.add-new-log-parser'), 'Add New button is not showing');
    assert.ok(find('.delete-log-parser'), 'Delete button is not showing');
  });

  test('Delete confirmation, query payload, and flash message', async function(assert) {
    assert.expect(7);
    const done = assert.async();
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{content/log-parser-rules/log-parsers/toolbar}}
    `);
    await click('.delete-log-parser button');
    const expectedMessage = translation.t('configure.logsParser.modals.deleteParser.confirm', { parserName: 'builtin' });
    assert.ok(find('.delete-log-parser .confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.delete-log-parser .confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'update');
      assert.equal(modelName, 'log-parser-rules');
      assert.deepEqual(query, {
        action: 'DELETE_PARSER',
        logDeviceParserName: 'builtin'
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('configure.logsParser.modals.deleteParser.success', { parserName: 'builtin' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message, expectedMessage);
      done();
    });
    await click('.modal-footer-buttons .is-primary button');
    await settled();
  });

  test('Delete shows an error flash message if the operation fails', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(0, true).build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{content/log-parser-rules/log-parsers/toolbar}}
    `);
    await click('.delete-log-parser button');
    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('configure.logsParser.modals.deleteParser.failure', { parserName: 'builtin' });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message, expectedMessage);
    });
    await click('.modal-footer-buttons .is-primary button');
  });

  test('Test tooltip for disabled delete button', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(false).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/log-parsers/toolbar}}`);
    assert.ok(find('div.toolTipMask'), 'toolTipMask is not showing');
    await triggerEvent('div.toolTipMask', 'mouseover');
    assert.ok(find('div.toolTip'), 'toolTip is not showing');
    await triggerEvent('div.toolTipMask', 'mouseout');
    assert.notOk(find('div.toolTip'), 'toolTip is showing');
  });
});