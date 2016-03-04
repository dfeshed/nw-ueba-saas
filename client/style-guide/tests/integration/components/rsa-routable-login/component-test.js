import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-routable-login', 'Integration | Component | rsa-routable-login', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-routable-login}}`);
  assert.equal(this.$('.rsa-login').length, 1);
});

test('the submit is disabled by default', function(assert) {
  this.render(hbs `{{rsa-routable-login}}`);
  assert.ok(this.$('button[type=submit]').is(':disabled'));
});

test('the submit is enabled after entering values', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  assert.notOk(this.$('button[type=submit]').is(':disabled'));
});
