import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-button', 'Integration | Component | rsa-form-button', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{#rsa-form-button}}Label{{/rsa-form-button}}`);
  assert.equal(this.$().text().trim(), 'Label');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-form-button}}Label{{/rsa-form-button}}`);
  const buttonCount = this.$().find('.rsa-form-button-wrapper').length;
  assert.equal(buttonCount, 1);
});

test('it includes the proper classes when isActive', function(assert) {
  this.render(hbs `{{#rsa-form-button isActive=true}}Label{{/rsa-form-button}}`);
  const buttonCount = this.$().find('.rsa-form-button-wrapper.is-active').length;
  assert.equal(buttonCount, 1);
});

test('it includes the proper attributes when a submit button isDisabled', function(assert) {
  this.render(hbs `{{#rsa-form-button type="submit" isDisabled=true}}Label{{/rsa-form-button}}`);
  const buttonCount = this.$().find('.rsa-form-button[disabled]').length;
  assert.equal(buttonCount, 1);
});

test('it includes the proper classes when isPrimary', function(assert) {
  this.render(hbs `{{#rsa-form-button isPrimary=true}}Label{{/rsa-form-button}}`);
  const button = this.$().find('.rsa-form-button-wrapper').first();
  assert.ok(button.hasClass('is-primary'));
});

test('it includes the proper classes when isDanger', function(assert) {
  this.render(hbs `{{#rsa-form-button isDanger=true}}Label{{/rsa-form-button}}`);
  const button = this.$().find('.rsa-form-button-wrapper').first();
  assert.ok(button.hasClass('is-danger'));
});

test('it includes the proper classes when isIconOnly', function(assert) {
  this.render(hbs `{{#rsa-form-button isIconOnly=true}}Label{{/rsa-form-button}}`);
  const button = this.$().find('.rsa-form-button-wrapper').first();
  assert.ok(button.hasClass('is-icon-only'));
});

test('it includes the proper classes when dropdown is defined', function(assert) {
  this.render(hbs `{{#rsa-form-button withDropdown=true}}Label{{/rsa-form-button}}`);
  const button = this.$().find('.rsa-form-button-wrapper').first();
  assert.ok(button.hasClass('with-dropdown'));
});
