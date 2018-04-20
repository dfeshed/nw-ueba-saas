import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | Routable Login', function(hooks) {
  setupRenderingTest(hooks);

  test('It renders', async function(assert) {
    assert.expect(1);

    await render(hbs`{{rsa-routable-login}}`);
    assert.equal(findAll('.rsa-login').length, 1);
  });

  test('Security banner not displayed', async function(assert) {
    assert.expect(1);

    await render(hbs`{{rsa-routable-login displayEula=false displaySecurityBanner=false}}`);
    assert.equal(findAll('.banner-content').length, 0);
  });

  test('Security banner displayed', async function(assert) {
    assert.expect(1);

    await render(hbs`{{rsa-routable-login displayEula=false displaySecurityBanner=true}}`);
    assert.equal(findAll('.banner-content').length, 1);
  });

  test('Security banner can be dismissed', async function(assert) {
    assert.expect(3);

    await render(hbs`{{rsa-routable-login displayEula=false displaySecurityBanner=true}}`);

    const buttons = findAll('.rsa-form-button');
    assert.equal(buttons.length, 1, 'One button exists');
    assert.equal(buttons[0].textContent.trim(), 'Agree', 'Button text is Agree');

    await click(buttons[0]);
    assert.equal(findAll('.banner-content').length, 0, 'Clicking the Accept button dismisses the login banner');
  });
});
