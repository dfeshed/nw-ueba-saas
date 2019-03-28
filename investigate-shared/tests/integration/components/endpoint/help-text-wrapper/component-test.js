import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/help-text-wrapper}}`);', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('help text wrapper renders', async function(assert) {

    await render(hbs`{{endpoint/help-text-wrapper}}`);

    assert.equal(findAll('.helper-text-wrapper').length, 1, 'it renders endpoint helper-text-wrapper');
  });

  test('help text wrapper renders', async function(assert) {
    const helpText = [
      { title: 'endpointRAR.helpText.title' },
      { subTitle: 'endpointRAR.helpText.downloadInstaller', helpText: 'endpointRAR.helpText.downloadInstallerContent' },
      { contentSeparator: 'endpointRAR.helpText.onceInstalled' },
      { subTitle: 'endpointRAR.helpText.addESH', helpText: 'endpointRAR.helpText.addESHContent' },
      { subTitle: 'endpointRAR.helpText.configureServers', helpText: 'endpointRAR.helpText.configureServersContent' }
    ];

    this.set('content', helpText);
    await render(hbs`{{endpoint/help-text-wrapper content=content}}`);

    assert.equal(find('.help-text-wrapper_title').textContent.trim(), 'Setup help', 'Help text title rendered');
    assert.equal(find('.help-text-wrapper_sub-title').textContent.trim(), 'Download Installer', 'Help text sub title rendered');
    assert.equal(find('.help-text-wrapper_help-text').textContent.trim().includes('Add a password to enable'), 1, 'Help text rendered');
    assert.equal(find('.help-text-wrapper_content-separator .help-text-wrapper_content').textContent.trim(), 'once installed', 'Help text separator content rendered');
  });

});
