import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-label', 'Integration | Component | rsa-content-label', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-content-label}}`);
  const labelCount = this.$().find('.rsa-content-label').length;
  assert.equal(labelCount, 1);
});

test('it sets the label', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo'}}`);
  const label = this.$().find('.rsa-content-label').text();
  assert.notEqual(label.indexOf('Foo'), -1);
});

test('it includes the proper classes when style is danger', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' style='danger'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-danger'));
});

test('it includes the proper classes when style is low', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' style='low'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-low'));
});

test('it includes the proper classes when style is medium', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' style='medium'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-medium'));
});

test('it includes the proper classes when style is high', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' style='high'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-high'));
});

test('it includes the proper classes when style is standard by default', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' }}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-standard'));
});

test('it includes the proper classes when set isDisabled to true', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' isDisabled= true}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when size is small by default', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-small-size'));
});

test('it includes the proper classes when size is medium', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' size='medium'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-medium-size'));
});

test('it includes the proper classes when size is large', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' size='large'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-large-size'));
});

test('it includes the proper classes when size is large', function(assert) {
  this.render(hbs `{{rsa-content-label label='Foo' size='large'}}`);
  const label = this.$().find('.rsa-content-label').first();
  assert.ok(label.hasClass('is-large-size'));
});

test('it includes the icon', function(assert) {
  this.render(hbs `{{#rsa-content-label label='Foo'}}{{rsa-icon name='account-circle-1'}}{{/rsa-content-label}}`);
  const iconCount = this.$().find('.rsa-content-label .rsa-icon').length;
  assert.equal(iconCount, 1);
});

test('it includes the close icon when click is defined', function(assert) {
  this.render(hbs `{{rsa-content-label click=true label='Foo'}}`);
  const iconCount = this.$().find('.rsa-content-label .rsa-icon-close').length;
  assert.equal(iconCount, 1);
});
