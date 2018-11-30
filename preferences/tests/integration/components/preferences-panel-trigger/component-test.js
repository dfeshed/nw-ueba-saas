import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import preferencesConfig from '../../../data/config';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitFor, render } from '@ember/test-helpers';
import wait from 'ember-test-helpers/wait';

const contentToRender = hbs `
  {{#rsa-application-content}}
    <grid responsive>
      <box class="col-xs-3">
        <aside>
          {{preferences-panel-trigger
            preferencesConfig=preferencesConfig
            publishPreferences=preferencesUpdated
          }}
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

module('Integration | Component | Preferences Panel Trigger', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    // this.registry.injection('component', 'i18n', 'service:i18n');
    // this.inject.service('redux');
    this.set('preferencesConfig', preferencesConfig);
    initialize(this.owner);
  });

  test('Preferences trigger renders correctly', async function(assert) {
    await render(hbs `
      {{preferences-panel-trigger
        preferencesConfig=preferencesConfig}}
    `);
    assert.equal(this.$('.rsa-preferences-panel-trigger').length, 1, 'Preference trigger component rendered.');
  });

  test('preferences trigger does not publish preferences on open', async function(assert) {
    this.set('preferencesUpdated', function() {
      assert.notOk(true, 'preference should not be published');
    });

    await render(contentToRender);

    await waitFor('.rsa-preferences-panel-trigger');
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    // TODO bring download back. Add meta download preference back
    await waitFor('.ember-power-select-selected-item', { count: 3 });
    assert.equal(this.$('.is-expanded').length, 1, 'preference panel opened without publishing preferences');
  });

  test('preferences trigger publishes updates to preferences', async function(assert) {
    const done = assert.async();
    this.set('preferencesUpdated', function(prefs) {
      assert.ok(prefs, 'preference should be published');
      done();
    });

    await render(contentToRender);

    await waitFor('.rsa-preferences-panel-trigger');
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    await waitFor('.rsa-form-radio-group-label');
    this.$('.rsa-form-radio-label.WALL').click();
  });

  test('preferences trigger publishes only updated preferences', async function(assert) {
    const done = assert.async();
    this.set('preferencesUpdated', function(prefs) {
      assert.equal(prefs.queryTimeFormat, 'WALL', 'queryTimeFormat should be set to WALL');
      assert.ok(!prefs.eventAnalysisPreferences, 'eventAnalysisPreferences field should not have been published');
      assert.ok(!prefs.eventPreferences, 'eventPreferences field should not have been published');
      done();
    });

    await render(contentToRender);

    await waitFor('.rsa-preferences-panel-trigger');
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    await waitFor('.rsa-form-radio-group-label');
    this.$('.rsa-form-radio-label.WALL').click();
  });

  test('preferences trigger does not publish preferences on close', async function(assert) {
    this.set('preferencesUpdated', function() {
      assert.notOk(true, 'preference should not be published');
    });

    await render(contentToRender);

    await waitFor('.rsa-preferences-panel-trigger');
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    await waitFor('.rsa-form-radio-group-label');
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    await wait();
    assert.equal(this.$('.is-expanded').length, 0, 'preference panel closed without publishing preferences');
  });
});
