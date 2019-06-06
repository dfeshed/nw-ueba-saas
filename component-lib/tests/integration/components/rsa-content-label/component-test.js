import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-content-label', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-content-label}}`);
    const labelCount = findAll('.rsa-content-label').length;
    assert.equal(labelCount, 1);
  });

  test('it sets the label', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo'}}`);
    const label = find('.rsa-content-label').textContent;
    assert.notEqual(label.indexOf('Foo'), -1);
  });

  test('it includes the proper classes when style is danger', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' style='danger'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-danger'));
  });

  test('it includes the proper classes when style is low', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' style='low'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-low'));
  });

  test('it includes the proper classes when style is medium', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' style='medium'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-medium'));
  });

  test('it includes the proper classes when style is high', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' style='high'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-high'));
  });

  test('it includes the proper classes when style is standard by default', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' }}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-standard'));
  });

  test('it includes the proper classes when set isDisabled to true', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' isDisabled= true}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-disabled'));
  });

  test('it includes the proper classes when size is small by default', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-small-size'));
  });

  test('it includes the proper classes when size is medium', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' size='medium'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-medium-size'));
  });

  test('it includes the proper classes when size is large', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' size='large'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-large-size'));
  });

  test('it includes the proper classes when size is large', async function(assert) {
    await render(hbs `{{rsa-content-label label='Foo' size='large'}}`);
    const label = find('.rsa-content-label');
    assert.ok(label.classList.contains('is-large-size'));
  });

  test('it includes the icon', async function(assert) {
    await render(hbs `{{#rsa-content-label label='Foo'}}{{rsa-icon name='account-circle-1'}}{{/rsa-content-label}}`);
    const iconCount = findAll('.rsa-content-label .rsa-icon').length;
    assert.equal(iconCount, 1);
  });

  test('it includes the close icon when click is defined', async function(assert) {
    await render(hbs `{{rsa-content-label click=true label='Foo'}}`);
    const iconCount = findAll('.rsa-content-label .rsa-icon-close-filled').length;
    assert.equal(iconCount, 1);
  });
});
