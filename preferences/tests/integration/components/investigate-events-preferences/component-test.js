import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { LOAD_PREFERENCES } from 'preferences/actions/types';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('investigate-events-preferences', 'Integration | Component | investigate events preferences', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    initialize(this);
  }
});

test('Show Loader', function(assert) {
  this.render(hbs`{{investigate-events-preferences isReady=false}}`);
  assert.equal(this.$('.rsa-loader-position').length, 1);
});

test('Render Investigate Event Preferences Template Correctly', function(assert) {
  this.render(hbs `{{investigate-events-preferences}}`);
  this.get('redux').dispatch({
    type: LOAD_PREFERENCES,
    payload: { eventsPreferences: { defaultAnalysisView: 'text' } }
  });
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Text Analysis');
  });
});

test('Get/Set the default event view value Correctly', function(assert) {
  this.render(hbs `{{investigate-events-preferences}}`);
  this.get('redux').dispatch({
    type: LOAD_PREFERENCES,
    payload: { eventsPreferences: { defaultAnalysisView: 'packet' } }
  });
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Packet Analysis');
  });
});
