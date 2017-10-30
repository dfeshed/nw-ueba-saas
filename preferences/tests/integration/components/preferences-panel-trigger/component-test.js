import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('preferences-panel-trigger', 'Integration | Component | Preferences Panel Trigger', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
  }
});

test('Preferences trigger renders correctly', function(assert) {
  this.set('launchFor', 'investigate-events');
  this.render(hbs `{{preferences-panel-trigger launchFor=launchFor}}`);

  assert.equal(this.$('.rsa-preferences-panel-trigger').length, 1, 'Preference trigger component rendered.');
});
