import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import preferencesConfig from '../../../data/config';
import sinon from 'sinon';
import { lookup } from 'ember-dependency-lookup';

moduleForComponent('preferences-panel', 'Integration | Component | Preferences Panel', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
    this.set('preferencesConfig', preferencesConfig);
    this.render(hbs `
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
      {{/rsa-application-content}}`);
  }
});

test('Preferences panel renders correctly', function(assert) {
  assert.equal(this.$('.rsa-preferences-panel').length, 1, 'Preference Panel rendered.');
});

test('Preferences panel opens correctly', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
  });
});

test('Preferences panel shows help icon', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    const contextualHelp = lookup('service:contextualHelp');
    const goToHelpSpy = sinon.stub(contextualHelp, 'goToHelp');
    assert.equal(this.$('.rsa-icon-help-circle-lined').length, 1, 'Need to display help icons.');
    this.$('.rsa-icon-help-circle-lined').trigger('click');
    sinon.assert.calledOnce(goToHelpSpy);
  });
});

test('Preferences panel closes correctly', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    this.$('.rsa-icon-close-filled').trigger('click');
    return wait().then(() => {
      assert.equal(this.$('.is-expanded').length, 0, 'Preference Panel closed.');
    });
  });
});

test('Preferences panel should not closes if click on panel again', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    assert.equal(this.$('.rsa-icon-help-circle-lined').length, 1, 'Need to display help icons.');
    this.$('.rsa-preferences-body-content').trigger('click');
    return wait().then(() => {
      assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel closed.');
      assert.equal(this.$('.rsa-icon-help-circle-lined').length, 1, 'Need to display help icons.');
    });
  });
});

test('Preferences panel closes correctly on application click', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    this.$('.testDiv').trigger('click');
    return wait().then(() => {
      assert.equal(this.$('.is-expanded').length, 0, 'Preference Panel closed.');
    });
  });
});
