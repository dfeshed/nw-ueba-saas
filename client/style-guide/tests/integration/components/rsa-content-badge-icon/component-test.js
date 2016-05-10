import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-badge-icon', 'Integration | Component | rsa-content-badge-icon', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon}}`);
  let badgeCount = this.$().find('.rsa-content-badge-icon').length;
  assert.equal(badgeCount, 1);
});

test('it sets the label', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon label='Foo'}}`);
  let label = this.$().find('h5').text();
  assert.notEqual(label.indexOf('Foo'), -1);
});

test('it includes the icon', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon icon="atomic-bomb" label='Foo'}}`);
  let icon = this.$().find('i');
  assert.ok(icon.hasClass('rsa-icon-atomic-bomb'));
});

test('it includes the proper classes when isDanger is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon icon="atomic-bomb" label='Foo' isDanger=true}}`);
  let badge = this.$().find('.rsa-content-badge-icon').first();
  assert.ok(badge.hasClass('is-danger'));
});

test('it includes the proper classes when isLow is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon icon="atomic-bomb" label='Foo' isLow=true}}`);
  let badge = this.$().find('.rsa-content-badge-icon').first();
  assert.ok(badge.hasClass('is-low'));
});

test('it includes the proper classes when isMedium is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon icon="atomic-bomb" label='Foo' isMedium=true}}`);
  let badge = this.$().find('.rsa-content-badge-icon').first();
  assert.ok(badge.hasClass('is-medium'));
});

test('it includes the proper classes when isHigh is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon icon="atomic-bomb" label='Foo' isHigh=true}}`);
  let badge = this.$().find('.rsa-content-badge-icon').first();
  assert.ok(badge.hasClass('is-high'));
});

test('it includes the proper classes when isPassive is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-icon icon="atomic-bomb" label='Foo' isPassive=true}}`);
  let badge = this.$().find('.rsa-content-badge-icon').first();
  assert.ok(badge.hasClass('is-passive'));
});
