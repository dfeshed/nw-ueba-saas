// import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

// const { Object: EmberObject } = Ember;

moduleForComponent('/rsa-application-user-preferences-panel', 'Integration | Component | rsa-application-user-preferences-panel', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-user-preferences-panel}}`);
  const pref = this.$().find('.rsa-application-user-preferences-panel').length;
  assert.equal(pref, 1);
});

// test('it includes all fields when session is authenticated', function(assert) {
//   this.set('session', EmberObject.create());
//   this.set('session.isAuthenticated', true);
//   this.render(hbs `{{rsa-application-user-preferences-panel isExpanded=true session=session}}`);
//   assert.equal(this.$().find('.js-test-language-select').length, 1);
//   assert.equal(this.$().find('.js-test-time-zone-select').length, 1);
//   assert.equal(this.$().find('.js-test-date-format-select').length, 1);
//   assert.equal(this.$().find('.js-test-default-landing-page-select').length, 1);
//   assert.equal(this.$().find('.time-format-radio-group').length, 1);
// });

// test('it includes all fields when session is not authenticated', function(assert) {
//   this.set('session', EmberObject.create());
//   this.set('session.isAuthenticated', false);
//   this.render(hbs `{{rsa-application-user-preferences-panel isExpanded=true session=session}}`);
//   assert.equal(this.$().find('.js-test-language-select').length, 1);
//   assert.equal(this.$().find('.js-test-time-zone-select').length, 1);
//   assert.equal(this.$().find('.js-test-date-format-select').length, 1);
//   assert.equal(this.$().find('.js-test-default-landing-page-select').length, 0);
//   assert.equal(this.$().find('.time-format-radio-group').length, 1);
// });
