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
  const labelCount = this.$().find('label').length === 1;
  assert.equal(labelCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-input}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-input'));
});

test('sets the value', function(assert) {
  this.render(hbs `{{rsa-form-input value='foo'}}`);
  const input = this.$().find('input').first();
  assert.ok(input.val('foo'));
});

test('sets the label', function(assert) {
  this.render(hbs `{{rsa-form-input label='foo'}}`);
  const label = this.$().find('.rsa-form-label').first().text().trim();
  assert.equal(label, 'foo');
});

test('sets the errorMessage', function(assert) {
  this.render(hbs `{{rsa-form-input label='foo' isError=true errorMessage='Bar'}}`);
  const error = this.$().find('.input-error').first().text().trim();
  assert.equal(error, 'Bar');
});

test('it includes the proper classes when isInline is true', function(assert) {
  this.render(hbs `{{rsa-form-input isInline=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-inline'));
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-input isDisabled=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-input isReadOnly=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-input isError=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it includes the proper classes when isSuccess is true', function(assert) {
  this.render(hbs `{{rsa-form-input isSuccess=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-success'));
});

test('it renders a placeholder', function(assert) {
  this.render(hbs `{{rsa-form-input placeholder='foo'}}`);
  const input = this.$().find('input').first();
  assert.equal(input.attr('placeholder'), 'foo');
});

test('it can be disabled', function(assert) {
  this.render(hbs `{{rsa-form-input isDisabled=true}}`);
  const disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('it is disabled when isReadOnly', function(assert) {
  this.render(hbs `{{rsa-form-input isReadOnly=true}}`);
  const disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('it calls closure action focusIn', function(assert) {
  assert.expect(1);
  this.on('focus', () => {
    assert.ok(true);
  });
  this.render(hbs `{{rsa-form-input focusIn=(action 'focus')}}`);
  this.$('input').focus();
});

test('it calls closure action focusOut', function(assert) {
  assert.expect(1);
  this.on('blur', () => {
    assert.ok(true);
  });
  this.render(hbs `{{rsa-form-input focusOut=(action 'blur')}}`);
  this.$('input').blur();
});

test('it does not show the error message if there is no error', function(assert) {
  this.render(hbs `{{rsa-form-input isError=false errorMessage='There was an error'}}`);
  assert.equal(this.$('.input-error').length, 0, 'The error message is not present');
});

test('it does show the error message if isError is true', function(assert) {
  this.render(hbs `{{rsa-form-input isError=true errorMessage='There was an error'}}`);
  assert.equal(this.$('.input-error').length, 1, 'The error message is present');
});

test('it takes focus when autofocus is true', async function(assert) {
  this.render(hbs `{{rsa-form-input value='foo' autofocus=true}}`);
  assert.equal(this.$('input[autofocus]').length, 1, 'expect component to be focused');
});