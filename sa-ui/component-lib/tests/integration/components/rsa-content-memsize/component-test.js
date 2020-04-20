import { find, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa content memsize', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component:rsa-content-memsize', 'i18n', 'service:i18n');
  });

  test('it renders with the correct class', async function(assert) {
    await render(hbs`{{rsa-content-memsize}}`);
    assert.equal(findAll('.rsa-content-memsize').length, 1);
  });

  test('it renders less than 1024 bytes in bytes', async function(assert) {
    await render(hbs`{{rsa-content-memsize size=10}}`);
    assert.equal(find('.size').textContent.trim(), 10);
    assert.equal(find('.rsa-content-memsize').getAttribute('title').trim(), '10 bytes');
  });

  test('it renders 1024 bytes or more in various sizes', async function(assert) {
    let bytes = 1024;
    this.set('size', bytes);
    await render(hbs`{{rsa-content-memsize size=size}}`);
    assert.equal(find('.size').textContent.trim().indexOf('1.'), 0);
    assert.equal(find('.rsa-content-memsize').getAttribute('title').trim(), `${bytes} bytes`);

    bytes *= 1025 * 2;
    this.set('size', bytes);
    assert.equal(find('.size').textContent.trim().indexOf('2.'), 0);
    assert.equal(find('.rsa-content-memsize').getAttribute('title').trim(), `${bytes} bytes`);

    bytes *= 1025;
    this.set('size', bytes);
    assert.equal(find('.size').textContent.trim().indexOf('2.'), 0);
    assert.equal(find('.rsa-content-memsize').getAttribute('title').trim(), `${bytes} bytes`);

    bytes *= 2;
    this.set('size', bytes);
    assert.equal(find('.size').textContent.trim().indexOf('4.'), 0);
    assert.equal(find('.rsa-content-memsize').getAttribute('title').trim(), `${bytes} bytes`);
  });
});

