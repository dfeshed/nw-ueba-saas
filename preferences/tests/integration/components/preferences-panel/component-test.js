import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import { lookup } from 'ember-dependency-lookup';

import preferencesConfig from '../../../data/config';

const contentToRender = hbs `
  {{#rsa-application-content}}
    <grid responsive>
      <box class="col-xs-3">
        <aside>
          {{preferences-panel-trigger preferencesConfig=preferencesConfig}}
          <div class='testDiv'>
            Panel Content
          </div>
        </aside>
      </box>
      <box class="col-xs-9">
        <page>
          {{preferences-panel}}
        </page>
      </box>
    </grid>
  {{/rsa-application-content}}
`;

module('Integration | Component | Preferences Panel', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(async function() {
    // this.inject.service('redux');
    // this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this.owner);
    this.set('preferencesConfig', preferencesConfig);
  });

  test('Preferences panel renders correctly', async function(assert) {
    await render(contentToRender);
    assert.equal(findAll('.rsa-preferences-panel').length, 1, 'Preference Panel rendered.');
  });

  test('Preferences panel opens correctly', async function(assert) {
    await render(contentToRender);
    await click('.rsa-icon-settings-1-filled');
    assert.equal(findAll('.is-expanded').length, 1, 'Preference Panel opened.');
  });

  test('Preferences panel shows help icon', async function(assert) {
    await render(contentToRender);
    await click('.rsa-icon-settings-1-filled');
    const contextualHelp = lookup('service:contextualHelp');
    const goToHelpSpy = sinon.stub(contextualHelp, 'goToHelp');
    assert.equal(findAll('.rsa-icon-help-circle-lined').length, 1, 'Need to display help icons.');
    await click('.rsa-icon-help-circle-lined');
    sinon.assert.calledOnce(goToHelpSpy);
  });

  test('Preferences panel closes correctly', async function(assert) {
    await render(contentToRender);
    await click('.rsa-icon-settings-1-filled');
    await click('.rsa-icon-close-filled');
    assert.equal(findAll('.is-expanded').length, 0, 'Preference Panel closed.');
  });

  test('Preferences panel should not closes if click on panel again', async function(assert) {
    await render(contentToRender);
    await click('.rsa-icon-settings-1-filled');
    assert.equal(findAll('.is-expanded').length, 1, 'Preference Panel opened.');
    assert.equal(findAll('.rsa-icon-help-circle-lined').length, 1, 'Need to display help icons.');
    await click('.rsa-preferences-panel');
    assert.equal(findAll('.is-expanded').length, 1, 'Preference Panel closed.');
    assert.equal(findAll('.rsa-icon-help-circle-lined').length, 1, 'Need to display help icons.');
  });

  test('Preferences panel closes correctly on application click', async function(assert) {
    await render(contentToRender);
    await click('.rsa-icon-settings-1-filled');
    assert.equal(findAll('.is-expanded').length, 1, 'Preference Panel opened.');
    await click('.testDiv');
    assert.equal(findAll('.is-expanded').length, 0, 'Preference Panel closed.');
  });
});