import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-form-button', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs `{{#rsa-form-button}}Label{{/rsa-form-button}}`);
    assert.equal(find('*').textContent.trim(), 'Label');
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{#rsa-form-button}}Label{{/rsa-form-button}}`);
    const buttonCount = this.$().find('.rsa-form-button-wrapper').length;
    assert.equal(buttonCount, 1);
  });

  test('it includes the proper classes when isActive', async function(assert) {
    await render(hbs `{{#rsa-form-button isActive=true}}Label{{/rsa-form-button}}`);
    const buttonCount = this.$().find('.rsa-form-button-wrapper.is-active').length;
    assert.equal(buttonCount, 1);
  });

  test('it includes the proper attributes when a submit button isDisabled', async function(assert) {
    await render(hbs `{{#rsa-form-button type="submit" isDisabled=true}}Label{{/rsa-form-button}}`);
    const buttonCount = this.$().find('.rsa-form-button[disabled]').length;
    assert.equal(buttonCount, 1);
  });

  test('it includes the proper classes when isPrimary', async function(assert) {
    await render(hbs `{{#rsa-form-button isPrimary=true}}Label{{/rsa-form-button}}`);
    const button = this.$().find('.rsa-form-button-wrapper').first();
    assert.ok(button.hasClass('is-primary'));
  });

  test('it includes the proper classes when isDanger', async function(assert) {
    await render(hbs `{{#rsa-form-button isDanger=true}}Label{{/rsa-form-button}}`);
    const button = this.$().find('.rsa-form-button-wrapper').first();
    assert.ok(button.hasClass('is-danger'));
  });

  test('it includes the proper classes when isIconOnly', async function(assert) {
    await render(hbs `{{#rsa-form-button isIconOnly=true}}Label{{/rsa-form-button}}`);
    const button = this.$().find('.rsa-form-button-wrapper').first();
    assert.ok(button.hasClass('is-icon-only'));
  });

  test('it includes the proper classes when dropdown is defined', async function(assert) {
    await render(hbs `{{#rsa-form-button withDropdown=true}}Label{{/rsa-form-button}}`);
    const button = this.$().find('.rsa-form-button-wrapper').first();
    assert.ok(button.hasClass('with-dropdown'));
  });
});
