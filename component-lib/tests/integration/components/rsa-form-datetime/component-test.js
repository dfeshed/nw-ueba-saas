import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-datetime', 'Integration | Component | rsa-form-datetime', {
  integration: true,
  beforeEach() {
    this.set('value', '1991-01-01T06:00:00.000Z');
  }
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
  assert.equal(this.$().find('input').length, 1);
});

test('it is a label', function(assert) {
  this.render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
  const labelCount = this.$().find('label').length === 1;
  assert.equal(labelCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-input'));
});

test('sets the value', function(assert) {
  this.render(hbs `{{rsa-form-datetime value='foo' onChange=(action (mut dateValue))}}`);
  const input = this.$().find('input').first();
  assert.ok(input.val('foo'));
});

test('sets the label', function(assert) {
  this.render(hbs `{{rsa-form-datetime label='foo' value=(readonly value) onChange=(action (mut dateValue))}}`);
  const label = this.$().find('.rsa-form-label').first().text().trim();
  assert.equal(label, 'foo');
});

test('sets the errorMessage', function(assert) {
  this.render(hbs `{{rsa-form-datetime label='foo' isError=true errorMessage='Bar' value=(readonly value) onChange=(action (mut dateValue))}}`);
  const error = this.$().find('.input-error').first().text().trim();
  assert.equal(error, 'Bar');
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isDisabled=true value=(readonly value) onChange=(action (mut dateValue))}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-datetime isError=true value=(readonly value) onChange=(action (mut dateValue))}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it renders a placeholder', function(assert) {
  this.render(hbs `{{rsa-form-datetime placeholder='foo' value=(readonly value) onChange=(action (mut dateValue))}}`);
  const input = this.$().find('input').first();
  assert.equal(input.attr('placeholder'), 'foo');
});

test('it can be disabled', function(assert) {
  this.render(hbs `{{rsa-form-datetime isDisabled=true value=(readonly value) onChange=(action (mut dateValue))}}`);
  const disabledCount = this.$().find('input[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});
