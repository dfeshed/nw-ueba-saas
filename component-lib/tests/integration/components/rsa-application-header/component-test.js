import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-application-header', 'Integration | Component | rsa-application-header', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-header}}`);
  const header = this.$().find('.rsa-application-header').length;
  assert.equal(header, 1);
});

test('it included a link to User Preferences', function(assert) {
  this.set('session', {
    isAuthenticated: true
  });

  this.render(hbs `{{rsa-application-header session=session}}`);

  assert.equal(this.$('.user-preferences-trigger').length, 1);
});

test('it does not include help link when contextualHelp.module is not populated', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{rsa-application-header}}`);

  assert.equal(this.$('.global-contextual-help').length, 0);
});

test('it includes help link when contextualHelp.module is populated', function(assert) {
  this.set('helpStub', {});
  this.set('helpStub.module', 'foo');

  this.render(hbs `<div id="modalDestination"></div>{{rsa-application-header contextualHelp=helpStub}}`);

  assert.equal(this.$('.global-contextual-help').length, 1);
});
