import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('/rsa-form-checkbox', 'Integration | Component | rsa-form-checkbox', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-checkbox}}`);
  assert.equal(this.$().find('input').length, 1);
});

test('it has a label', function(assert) {
  this.render(hbs `{{rsa-form-checkbox label="Foo"}}`);
  const label = this.$().find('.rsa-form-label').text();
  assert.equal(label, 'Foo');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-checkbox}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-checkbox'));
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-checkbox isDisabled=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-checkbox isReadOnly=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-checkbox isError=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it updates the value', function(assert) {
  this.set('model', false);
  this.render(hbs `{{rsa-form-checkbox value=model}}`);
  this.$('input:first').prop('checked', true).trigger('change');
  const that = this;
  return wait().then(function() {
    assert.equal(that.get('model'), true);
  });
});
