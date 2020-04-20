import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-content-definition', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-content-definition}}`);
    const contentCount = findAll('.rsa-content-definition').length;
    assert.equal(contentCount, 1);
  });

  test('it includes the proper inner element', async function(assert) {
    await render(hbs `{{#rsa-content-definition}}
    <p class='inner-element-class'>something</p>
    {{/rsa-content-definition}}`);
    const contentCount = findAll('.inner-element-class').length;
    assert.equal(contentCount, 1, 'Checking inner content is displayed');
  });
});