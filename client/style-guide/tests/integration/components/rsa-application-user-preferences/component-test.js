import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('/rsa-application-user-preferences', 'Integration | Component | rsa-application-user-preferences', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-user-preferences}}`);
  let pref = this.$().find('.user-preferences-panel').length;
  assert.equal(pref, 1);
});

test('it includes all fields when session is authenticated', function(assert) {
  this.set('session', Ember.Object.create());
  this.set('session.isAuthenticated', true);
  this.render(hbs `{{rsa-application-user-preferences session=session}}`);
  assert.equal(this.$().find('.js-test-new-password').length, 1);
  assert.equal(this.$().find('.js-test-confirm-password').length, 1);
  assert.equal(this.$().find('.js-test-language-select').length, 1);
  assert.equal(this.$().find('.js-test-time-zone-select').length, 1);
  assert.equal(this.$().find('.js-test-date-format-select').length, 1);
  assert.equal(this.$().find('.js-test-default-landing-page-select').length, 1);
  assert.equal(this.$().find('.time-format-radio-group').length, 1);
  assert.equal(this.$().find('.theme-radio-group').length, 1);
  assert.equal(this.$().find('.spacing-radio-group').length, 1);
  assert.equal(this.$().find('.js-test-notifications-checkbox').length, 1);
  assert.equal(this.$().find('.js-test-context-menus-checkbox').length, 1);
});

test('it includes all fields when session is not authenticated', function(assert) {
  this.set('session', Ember.Object.create());
  this.set('session.isAuthenticated', false);
  this.render(hbs `{{rsa-application-user-preferences session=session}}`);
  assert.equal(this.$().find('.js-test-new-password').length, 0);
  assert.equal(this.$().find('.js-test-confirm-password').length, 0);
  assert.equal(this.$().find('.js-test-language-select').length, 1);
  assert.equal(this.$().find('.js-test-time-zone-select').length, 1);
  assert.equal(this.$().find('.js-test-date-format-select').length, 1);
  assert.equal(this.$().find('.js-test-default-landing-page-select').length, 0);
  assert.equal(this.$().find('.time-format-radio-group').length, 1);
  assert.equal(this.$().find('.theme-radio-group').length, 1);
  assert.equal(this.$().find('.spacing-radio-group').length, 1);
  assert.equal(this.$().find('.js-test-notifications-checkbox').length, 0);
  assert.equal(this.$().find('.js-test-context-menus-checkbox').length, 0);
});

test('it toggles submit button disabled with changes', function(assert) {
  this.set('withoutChanges', true);
  this.render(hbs `{{rsa-application-user-preferences withoutChanges=withoutChanges}}`);
  assert.equal(this.$().find('.js-test-apply[disabled]').length, 1);
  this.set('withoutChanges', false);
  assert.equal(this.$().find('.js-test-apply[disabled]').length, 0);
});

test('it toggles withoutChanges after clicking revert', function(assert) {
  this.set('withoutChanges', false);
  this.render(hbs `{{rsa-application-user-preferences withoutChanges=withoutChanges}}`);
  this.$().find('.js-test-revert span').click();
  let _this = this;
  return wait().then(function() {
    return assert.equal(_this.get('withoutChanges'), true);
  });
});
