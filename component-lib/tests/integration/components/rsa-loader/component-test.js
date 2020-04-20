import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | rsa-loader', function(hooks) {
  setupRenderingTest(hooks);

  test('The Loader component loads properly with all of the required elements.', async function(assert) {
    await render(hbs `{{rsa-loader}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'Testing to see if the .rsa-loader__container class exists.');
    assert.equal(findAll('.rsa-loader__wheel').length, 1, 'Testing to see if the .rsa-loader__wheel class exists.');
    assert.equal(findAll('.rsa-loader__text').length, 1, 'Testing to see if the .rsa-loader__text class exists.');
  });

  test('The Loader component properly renders the proper size class given a size attribute.', async function(assert) {
    await render(hbs `{{rsa-loader}}`);
    assert.equal(findAll('.is-small').length, 1, 'Testing to see if the loader is rendered using the small class.');

    await render(hbs `{{rsa-loader size='medium'}}`);
    assert.equal(findAll('.is-medium').length, 1, 'Testing to see if the loader is rendered using the medium class.');

    await render(hbs `{{rsa-loader size='large'}}`);
    assert.equal(findAll('.is-large').length, 1, 'Testing to see if the loader is rendered using the large class.');

    await render(hbs `{{rsa-loader size='larger'}}`);
    assert.equal(findAll('.is-larger').length, 1, 'Testing to see if the loader is rendered using the larger class.');

    await render(hbs `{{rsa-loader size='largest'}}`);
    assert.equal(findAll('.is-largest').length, 1, 'Testing to see if the loader is rendered using the largest class.');
  });

  test('The Loader component properly renders a label given the label attribute.', async function(assert) {
    await render(hbs `{{rsa-loader label='Gathering Data'}}`);
    assert.equal(find('.rsa-loader__text').textContent.trim(), 'Gathering Data', 'Testing to see if the proper label is applied to the loader.');
  });

  test('The Loader component properly renders properly given empty attribute values.', async function(assert) {
    await render(hbs `{{rsa-loader size='' label=''}}`);
    assert.equal(findAll('.is-small').length, 1, 'Testing to see if the loader is rendered using the small class.');
    assert.equal(find('.rsa-loader__text').textContent.trim(), '', 'Testing to see if the label attribute is ignored.');
  });
});