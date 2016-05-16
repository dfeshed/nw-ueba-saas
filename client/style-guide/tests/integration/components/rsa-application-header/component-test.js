import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-application-header', 'Integration | Component | rsa-application-header', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-header}}`);
  let header = this.$().find('.rsa-application-header').length;
  assert.equal(header, 1);
});

test('it included a link to User Preferences', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{rsa-application-header}}`);
  this.$('.user-preferences-trigger').click();
  this.$('.js-test-user-preferences-modal').click();
  assert.equal(this.$('.js-test-user-preferences-modal').length, 1);
});