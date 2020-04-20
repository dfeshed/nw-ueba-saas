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
      { subTitle: 'endpointRAR.helpText.whatNext', helpText: 'endpointRAR.helpText.whatNextContentStep1' }
    ];

    this.set('content', helpText);
    await render(hbs`{{endpoint/help-text-wrapper content=content}}`);

    assert.equal(find('.help-text-wrapper_title').textContent.trim(), 'Quick Help', 'Help text title rendered');
    assert.equal(find('.help-text-wrapper_sub-title').textContent.trim(), 'What next?', 'Help text sub title rendered');
    assert.equal(find('.help-text-wrapper_help-text').textContent.trim().includes('1. Specify the password and download the Relay Server installer. The same installer can be used for multiple Relay server installation.'), 1, 'Help text rendered');
  });

});
