import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-input', 'Integration | Component | rsa-form-input', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-input}}`);
  assert.equal(this.$().find('input').length, 1);
});

test('it is a label', function(assert) {
  this.render(hbs `{{rsa-form-input}}`);
  let labelCount = this.$().find('label').length === 1;
  assert.equal(labelCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-input}}`);
  let label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-input'));
});

test('sets the value', function(assert) {
  this.render(hbs `{{rsa-form-input value='foo'}}`);
  let input = this.$().find('input').first();
  assert.ok(input.val('foo'));
});

test('sets the label', function(assert) {
  this.render(hbs `{{rsa-form-input label='foo'}}`);
  let label = this.$().find('.rsa-form-label').first();
  assert.equal(label.text(), 'foo');
});

test('sets the errorMessage', function(assert) {
  this.render(hbs `{{rsa-form-input label='foo' isError=true errorMessage='Bar'}}`);
  let error = this.$().find('.rsa-form-error').first();
  assert.equal(error.text(), 'Bar');
});

test('it includes the proper classes when isInline is true', function(assert) {
  this.render(hbs `{{rsa-form-input isInline=true}}`);
  let label = this.$().find('label').first();
  assert.ok(label.hasClass('is-inline'));
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-input isDisabled=true}}`);
  let label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-input isReadOnly=true}}`);
  let label = this.$().find('label').first();
  assert.ok(label.hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-input isError=true}}`);
  let label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it includes the proper classes when isSuccess is true', function(assert) {
  this.render(hbs `{{rsa-form-input isSuccess=true}}`);
  let label = this.$().find('label').first();
  assert.ok(label.hasClass('is-success'));
});

test('it renders a placeholder', function(assert) {
  this.render(hbs `{{rsa-form-input placeholder='foo'}}`);
  let input = this.$().find('input').first();
  assert.equal(input.attr('placeholder'), 'foo');
});

test('it can be disabled', function(assert) {
  this.render(hbs `{{rsa-form-input isDisabled=true}}`);
  let disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('it is disabled when isReadOnly', function(assert) {
  this.render(hbs `{{rsa-form-input isReadOnly=true}}`);
  let disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});
