import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import preferencesConfig from '../../../data/config';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import wait from 'ember-test-helpers/wait';

moduleForComponent('preferences-panel-trigger', 'Integration | Component | Preferences Panel Trigger', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
    this.set('preferencesConfig', preferencesConfig);
    initialize(this);
  }
});

const contentToRender = hbs `
      {{#rsa-application-content}}
        <grid responsive>
          <box class="col-xs-3">
            <aside>
              {{preferences-panel-trigger
                preferencesConfig=preferencesConfig
                publishPreferences=(action 'preferencesUpdated')}}              
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
      {{/rsa-application-content}}`;

test('Preferences trigger renders correctly', function(assert) {
  this.render(hbs `{{preferences-panel-trigger preferencesConfig=preferencesConfig}}`);
  assert.equal(this.$('.rsa-preferences-panel-trigger').length, 1, 'Preference trigger component rendered.');
});

test('preferences trigger does not publish preferences on open', function(assert) {
  this.on('preferencesUpdated', function() {
    assert.notOk(true, 'preference should not be published');
  });

  this.render(contentToRender);

  return waitFor('.rsa-preferences-panel-trigger').then(() => {
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    return waitFor('.ember-power-select-selected-item', { count: 3 }).then(() => {
      assert.equal(this.$('.is-expanded').length, 1, 'preference panel opened without publishing preferences');
    });
  });
});

test('preferences trigger publishes updates to preferences', function(assert) {
  const done = assert.async();
  this.on('preferencesUpdated', function(prefs) {
    assert.ok(prefs, 'preference should be published');
    done();
  });

  this.render(contentToRender);

  return waitFor('.rsa-preferences-panel-trigger').then(() => {
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    return waitFor('.rsa-form-radio-group-label').then(() => {
      this.$('.rsa-form-radio-label.WALL').click();
    });
  });
});

test('preferences trigger publishes only updated preferences', function(assert) {
  const done = assert.async();
  this.on('preferencesUpdated', function(prefs) {
    assert.equal(prefs.queryTimeFormat, 'WALL', 'queryTimeFormat should be set to WALL');
    assert.ok(!prefs.eventAnalysisPreferences, 'eventAnalysisPreferences field should not have been published');
    assert.ok(!prefs.eventPreferences, 'eventPreferences field should not have been published');
    done();
  });

  this.render(contentToRender);

  return waitFor('.rsa-preferences-panel-trigger').then(() => {
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    return waitFor('.rsa-form-radio-group-label').then(() => {
      this.$('.rsa-form-radio-label.WALL').click();
    });
  });
});

test('preferences trigger does not publish preferences on close', function(assert) {
  this.on('preferencesUpdated', function() {
    assert.notOk(true, 'preference should not be published');
  });

  this.render(contentToRender);

  return waitFor('.rsa-preferences-panel-trigger').then(() => {
    this.$('.rsa-icon-settings-1-filled').trigger('click');
    return waitFor('.rsa-form-radio-group-label').then(() => {
      this.$('.rsa-icon-settings-1-filled').trigger('click');
      return wait().then(() => {
        assert.equal(this.$('.is-expanded').length, 0, 'preference panel closed without publishing preferences');
      });
    });
  });
});