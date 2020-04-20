import { findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-application-header', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-application-header}}`);
    const header = findAll('.rsa-application-header').length;
    assert.equal(header, 1);
  });

  test('it included a link to User Preferences', async function(assert) {
    this.set('session', {
      isAuthenticated: true
    });

    await render(hbs `{{rsa-application-header session=session}}`);

    assert.equal(findAll('.user-preferences-trigger').length, 1);
  });

  test('it does not include help link when contextualHelp.module is not populated', async function(assert) {
    await render(hbs `<div id="modalDestination"></div>{{rsa-application-header}}`);

    assert.equal(findAll('.global-contextual-help').length, 0);
  });

  test('it includes help link when contextualHelp.module is populated', async function(assert) {
    this.set('helpStub', {});
    this.set('helpStub.module', 'foo');

    await render(hbs `<div id="modalDestination"></div>{{rsa-application-header contextualHelp=helpStub}}`);

    assert.equal(findAll('.global-contextual-help').length, 1);
  });
});
