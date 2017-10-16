import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('preferences-panel', 'Integration | Component | Preferences Panel', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('Preferences panel renders correctly', function(assert) {
  this.set('launchFor', 'events');
  this.set('isExpanded', false);
  this.render(hbs `{{preferences-panel launchFor=launchFor isExpanded=isExpanded}}`);

  assert.equal(this.$('.rsa-preferences-panel').length, 1, 'Preference Panel rendered.');
});

test('Preferences panel opens correctly', function(assert) {
  this.set('launchFor', 'events');
  this.set('isExpanded', true);
  this.render(hbs `{{preferences-panel launchFor=launchFor isExpanded=isExpanded}}`);

  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
  });
});

test('Preferences panel closes correctly', function(assert) {
  this.set('launchFor', 'events');
  this.set('isExpanded', true);
  this.render(hbs `{{preferences-panel launchFor=launchFor isExpanded=isExpanded}}`);

  this.set('isExpanded', false);
  return wait().then(() => {
    assert.equal(this.$('.is-expanded').length, 0, 'Preference Panel closed.');
  });
});