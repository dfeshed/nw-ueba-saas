import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-checkbox', 'Integration | Component | rsa-form-checkbox', {
  integration: true
});

test('has the base class', function(assert) {
  this.render(hbs `{{rsa-form-checkbox}}`);
  assert.ok(this.$('input').hasClass('rsa-form-checkbox'));
});

test('has the checked class', function(assert) {
  this.render(hbs `{{rsa-form-checkbox checked=true}}`);
  assert.ok(this.$('input').hasClass('checked'));
});

test('has the disabled class', function(assert) {
  this.render(hbs `{{rsa-form-checkbox disabled=true}}`);
  assert.ok(this.$('input').hasClass('disabled'));
});
