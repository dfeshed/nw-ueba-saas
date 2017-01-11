import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('/rsa-form-checkbox', 'Integration | Component | rsa-form-checkbox-2', {
  integration: true
});

test('has the base class', function(assert) {
  this.render(hbs `{{rsa-form-checkbox-2}}`);
  assert.ok(this.$().hasClass('rsa-form-checkbox-2'));
});

test('has the checked class', function(assert) {
  this.render(hbs `{{rsa-form-checkbox-2 checked=true}}`);
  assert.ok(this.$().hasClass('checked'));
});

test('has the disabled class', function(assert) {
  this.render(hbs `{{rsa-form-checkbox-2 disabled=true}}`);
  assert.ok(this.$().hasClass('disabled'));
});
