import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-update-indicator', 'Integration | Component | rsa-update-indicator', {
  integration: true
});

test('The Update Indicator component in Default mode loads properly with all of the required elements.', function(assert) {
  this.set('mock', [ { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true } ]);
  this.render(hbs `{{~ rsa-update-indicator model=mock updateKey='asyncUpdate' ~}}`);

  assert.equal(this.$('.rsa-update-indicator__divider').length, 1, 'Testing to ensure that the .rsa-update-indicator__divider class exists.');
  assert.equal(this.$('.rsa-update-indicator__dot').length, 1, 'Testing to ensure that the .rsa-update-indicator__dot class exists.');
  assert.equal(this.$('.rsa-update-indicator__label').length, 1, 'Testing to ensure that the .rsa-update-indicator__label class exists.');
});

test('The Update Indicator component in Icon Only mode loads properly with all of the required elements.', function(assert) {
  this.set('mock', [{ 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }]);
  this.render(hbs `{{~ rsa-update-indicator model=mock isIconOnly=true onTile=true updateKey='asyncUpdate' ~}}`);

  assert.equal(this.$('.rsa-update-indicator__divider').length, 0, 'Testing to ensure that the .rsa-update-indicator__divider class does not exist.');
  assert.equal(this.$('.rsa-update-indicator__dot').length, 1, 'Testing to ensure that the .rsa-update-indicator__dot class exists.');
  assert.equal(this.$('.rsa-update-indicator__label').length, 0, 'Testing to ensure that the .rsa-update-indicator__label class does not exist.');
});

test('The Update Indicator component in Default mode is hidden when no updateKey is passed.', function(assert) {
  this.set('mock', [ { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true } ]);
  this.render(hbs `{{~ rsa-update-indicator model=mock ~}}`);

  assert.equal(this.$('.rsa-update-indicator.is-hidden').length, 1, 'Testing to ensure that the .rsa-update-indicator element is hidden.');
});

test('When no updates are available, the update indicator is not shown.', function(assert) {
  this.set('mock', [{ 'id': 1 },{ 'id': 2 },{ 'id': 3 },{ 'id': 4 }]);
  this.render(hbs `{{~ rsa-update-indicator model=mock updateKey='asyncUpdate' ~}}`);

  assert.equal(this.$('.rsa-update-indicator.is-hidden').length, 1, 'Testing to ensure that the .rsa-update-indicator class is also hidden.');
});

test('When only one update is available, the update indicator in default mode is shown in singular grammatical form.', function(assert) {
  this.set('mock', [{ 'id': 1, 'asyncUpdate': true },{ 'id': 2 },{ 'id': 3 },{ 'id': 4 }]);
  this.render(hbs `{{~ rsa-update-indicator model=mock updateKey='asyncUpdate' ~}}`);

  assert.equal(this.$('.rsa-update-indicator__label').html(), '1&nbsp;update', 'Testing to ensure that the .rsa-update-indicator__label element shows only one update in singular grammatical form.');
});

test('When multiple updates are available, the update indicator in default mode is shown in plural grammatical form.', function(assert) {
  this.set('mock', [{ 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }, { 'asyncUpdate': true }]);
  this.render(hbs `{{~ rsa-update-indicator model=mock updateKey='asyncUpdate' ~}}`);

  assert.equal(this.$('.rsa-update-indicator__label').html(), '10&nbsp;updates', 'Testing to ensure that the .rsa-update-indicator__label element shows ten updates in plural grammatical form.');
});
