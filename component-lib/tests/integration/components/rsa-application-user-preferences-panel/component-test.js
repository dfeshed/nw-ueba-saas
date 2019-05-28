import { module, test } from 'qunit';
// import Ember from 'ember';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-application-user-preferences-panel', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-application-user-preferences-panel}}`);
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
});
