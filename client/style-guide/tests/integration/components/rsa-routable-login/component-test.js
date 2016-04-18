import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

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

test('the password reset is disabled without a user name', function(assert) {
  this.set('username', 'foo');
  this.render(hbs `{{rsa-routable-login username=username password='bar'}}`);
  this.$('.lost-password').click();
  this.set('username', null);
  assert.ok(this.$('button[type=submit]').is(':disabled'));
});

test('the login transitions after clicking Lost Password?', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  return wait().then(function() {
    assert.ok(this.$('form').hasClass('lost-password'));
  });
});

test('the login transitions after clicking Lost Password? then canceling', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  this.$('button:first span').click();
  return wait().then(function() {
    assert.ok(this.$('form').hasClass('login'));
  });
});

test('the login transitions after clicking Lost Password? then submitting', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  this.$('button[type=submit]').click();
  return wait().then(function() {
    assert.equal(this.$('.thank-you').length, 1);
  });
});

test('the login transitions after clicking Lost Password? then submitting then clicking Return to Login', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  this.$('button:last span').click();
  this.$('button span').click();
  return wait().then(function() {
    assert.ok(this.$('form').hasClass('login'));
  });
});

test('the has-error class is added to .login-wrapper when hasError is true', function(assert) {
  this.render(hbs `{{rsa-routable-login hasError=true}}`);
  assert.ok(this.$('.login-wrapper').hasClass('has-error'));
});

