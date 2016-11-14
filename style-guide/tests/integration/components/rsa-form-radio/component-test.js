import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('/rsa-form-radio', 'Integration | Component | rsa-form-radio', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-radio}}`);
  assert.equal(this.$().find('input').length, 1);
});

test('it has a label', function(assert) {
  this.render(hbs `{{rsa-form-radio label="Foo"}}`);
  const label = this.$().find('.rsa-form-label').text();
  assert.equal(label, 'Foo');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-radio}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-radio'));
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-radio isDisabled=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-radio isReadOnly=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-radio isError=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it updates the value', function(assert) {
  this.set('model', null);
  this.render(hbs `{{rsa-form-radio model=model value='foo'}}{{rsa-form-radio model=model value='bar'}}`);
  this.$('input:first').prop('checked', true).trigger('change');
  const that = this;
  return wait().then(function() {
    assert.equal(that.get('model'), 'foo');
  });
});
