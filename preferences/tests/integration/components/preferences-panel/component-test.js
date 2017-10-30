import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import * as ACTION_TYPES from 'preferences/actions/types';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('preferences-panel', 'Integration | Component | Preferences Panel', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
  }
});

test('Preferences panel renders correctly', function(assert) {
  this.render(hbs `{{preferences-panel}}`);

  assert.equal(this.$('.rsa-preferences-panel').length, 1, 'Preference Panel rendered.');
});

test('Preferences panel opens correctly', function(assert) {
  this.render(hbs `{{preferences-panel}}`);
  this.get('redux').dispatch({
    type: ACTION_TYPES.TOGGLE_PREFERENCES_PANEL,
    payload: 'investigate-events'
  });
  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
  });
});

test('Preferences panel closes correctly', function(assert) {
  this.render(hbs `{{preferences-panel}}`);
  this.get('redux').dispatch({
    type: ACTION_TYPES.TOGGLE_PREFERENCES_PANEL,
    payload: 'investigate-events'
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.CLOSE_PREFERENCES_PANEL
  });
  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 0, 'Preference Panel closed.');
  });
});
