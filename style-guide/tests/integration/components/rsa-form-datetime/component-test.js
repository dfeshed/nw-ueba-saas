import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-datetime', 'Integration | Component | rsa-form-datetime', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-datetime}}`);
  assert.equal(this.$().find('input').length, 1);
});

test('it is a label', function(assert) {
  this.render(hbs `{{rsa-form-datetime}}`);
  const labelCount = this.$().find('label').length === 1;
  assert.equal(labelCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-datetime}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-input'));
});

test('sets the value', function(assert) {
  this.render(hbs `{{rsa-form-datetime value='foo'}}`);
  const input = this.$().find('input').first();
  assert.ok(input.val('foo'));
});

test('sets the label', function(assert) {
  this.render(hbs `{{rsa-form-datetime label='foo'}}`);
  const label = this.$().find('.rsa-form-label').first().text().trim();
  assert.equal(label, 'foo');
});

test('sets the errorMessage', function(assert) {
  this.render(hbs `{{rsa-form-datetime label='foo' isError=true errorMessage='Bar'}}`);
  const error = this.$().find('.error-icon').first().attr('title').trim();
  assert.equal(error, 'Bar');
});

test('it includes the proper classes when isInline is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isInline=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-inline'));
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isDisabled=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isReadOnly=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isError=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it includes the proper classes when isSuccess is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isSuccess=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-success'));
});

test('it renders a placeholder', function(assert) {
  this.render(hbs `{{rsa-form-datetime placeholder='foo'}}`);
  const input = this.$().find('input').first();
  assert.equal(input.attr('placeholder'), 'foo');
});

test('it can be disabled', function(assert) {
  this.render(hbs `{{rsa-form-datetime isDisabled=true}}`);
  const disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('it is disabled when isReadOnly', function(assert) {
  this.render(hbs `{{rsa-form-datetime isReadOnly=true}}`);
  const disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('MM/DD/YYYY should be the default format for the input', function(assert) {
  this.render(hbs `{{rsa-form-datetime value="Mon Nov 14 2016 15:15:22 GMT-0500 (EST)"}}`);
  assert.equal(this.$().find('input')[0].value, '11/14/2016');
});

test('format of the input is changeable', function(assert) {
  this.render(hbs `{{rsa-form-datetime dateFormat='YYYY.DD.MM' value="Mon Nov 14 2016 15:15:22 GMT-0500 (EST)"}}`);
  assert.equal(this.$().find('input')[0].value, '2016.14.11');
});
