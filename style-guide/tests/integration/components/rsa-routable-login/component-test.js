import { moduleForComponent, test, skip } from 'ember-qunit';
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
  this.render(hbs `{{rsa-routable-login displayEula=false}}`);
  assert.ok(this.$('button[type=submit]').is(':disabled'));
});

test('the submit is enabled after entering values', function(assert) {
  this.render(hbs `{{rsa-routable-login displayEula=false username='foo' password='bar'}}`);
  assert.notOk(this.$('button[type=submit]').is(':disabled'));
});

test('eula can be displayed', function(assert) {
  this.render(hbs `{{rsa-routable-login displayEula=true}}`);
  assert.equal(this.$('.eula-content').length, 1);
});

test('eula can be bypassed', function(assert) {
  this.render(hbs `{{rsa-routable-login displayEula=false}}`);
  assert.equal(this.$('.eula-content').length, 0);
});

skip('the password reset is disabled without a user name', function(assert) {
  this.set('username', 'foo');
  this.render(hbs `{{rsa-routable-login username=username password='bar'}}`);
  this.$('.lost-password').click();
  this.set('username', null);
  assert.ok(this.$('button[type=submit]').is(':disabled'));
});

skip('the login transitions after clicking Lost Password?', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  return wait().then(() => {
    assert.ok(this.$('form').hasClass('lost-password'));
  });
});

skip('the login transitions after clicking Lost Password? then canceling', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  this.$('.rsa-form-button:first').click();
  return wait().then(() => {
    assert.ok(this.$('form').hasClass('login'));
  });
});

skip('the login transitions after clicking Lost Password? then submitting', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' willRequestPasswordReset=true}}`);
  this.$('.lost-password').click();
  this.$('.rsa-form-button[type=submit]').click();
  return wait().then(() => {
    assert.equal(this.$('.thank-you').length, 1);
  });
});

skip('the login transitions after clicking Lost Password? then submitting then clicking Return to Login', function(assert) {
  this.render(hbs `{{rsa-routable-login username='foo' password='bar'}}`);
  this.$('.lost-password').click();
  this.$('.rsa-form-button[type=submit]').click();
  this.$('.rsa-form-button').click();
  return wait().then(() => {
    assert.ok(this.$('form').hasClass('login'));
  });
});

test('the has-error class is added to .login-wrapper when hasError is true', function(assert) {
  this.render(hbs `{{rsa-routable-login hasError=true}}`);
  assert.ok(this.$('.login-wrapper').hasClass('has-error'));
});
