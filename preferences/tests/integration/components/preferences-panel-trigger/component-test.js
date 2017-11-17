import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('preferences-panel-trigger', 'Integration | Component | Preferences Panel Trigger', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
    initialize(this);
  }
});

test('Preferences trigger renders correctly', function(assert) {
  this.set('launchFor', 'investigate-events');
  this.render(hbs `{{preferences-panel-trigger launchFor=launchFor}}`);

  assert.equal(this.$('.rsa-preferences-panel-trigger').length, 1, 'Preference trigger component rendered.');
});

test('Preferences trigger publishes change in preferences', function(assert) {
  const done = assert.async();
  this.set('launchFor', 'some-module');

  this.on('preferencesUpdated', function({ somePreference }) {
    assert.ok(somePreference, 'Correct preferences must be published');
    done();
  });
  this.render(hbs `{{preferences-panel-trigger launchFor=launchFor publishPreferences=(action 'preferencesUpdated')}}`);

  new DataHelper(this.get('redux')).initializeData().savePreference({ somePreference: true });
});
