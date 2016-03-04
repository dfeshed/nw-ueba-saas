import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-badge-score', 'Integration | Component | rsa-content-badge-score', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-content-badge-score}}`);
  let badgeCount = this.$().find('.rsa-content-badge-score').length;
  assert.equal(badgeCount, 1);
});

test('it sets the label', function(assert) {
  this.render(hbs `{{rsa-content-badge-score label='Foo'}}`);
  let label = this.$().find('.label').text();
  assert.notEqual(label.indexOf('Foo'), -1);
});

test('it sets the score', function(assert) {
  this.render(hbs `{{rsa-content-badge-score score='9.9'}}`);
  let label = this.$().find('.score').text();
  assert.notEqual(label.indexOf('9.9'), -1);
});

test('it includes the proper classes when there is no label', function(assert) {
  this.render(hbs `{{rsa-content-badge-score icon="atomic-bomb"}}`);
  let badge = this.$().find('.rsa-content-badge-score').first();
  assert.ok(badge.hasClass('hide-label'));
});

test('it includes the proper classes when isDanger is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-score isDanger=true}}`);
  let badge = this.$().find('.rsa-content-badge-score').first();
  assert.ok(badge.hasClass('is-danger'));
});
