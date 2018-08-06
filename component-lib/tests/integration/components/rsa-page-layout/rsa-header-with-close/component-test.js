import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-header-with-close', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{rsa-page-layout/rsa-header-with-close}}`);
    assert.equal(find('.rsa-header .title').textContent.trim(), 'Title', 'Default title is displayed');

    this.set('title', 'Filters');
    await render(hbs`{{rsa-page-layout/rsa-header-with-close title=title}}`);
    assert.equal(find('.rsa-header .title').textContent.trim(), 'Filters', 'Custom title is displayed');

    // Template block usage:
    await render(hbs`
      {{#rsa-page-layout/rsa-header-with-close}}
        template block text
      {{/rsa-page-layout/rsa-header-with-close}}
    `);

    assert.equal(this.element.textContent.trim(), 'template block text');
  });

  test('clicking close button calls the closeAction', async function(assert) {
    assert.expect(1);
    this.set('closeAction', () => {
      assert.ok(true, 'closeAction is called');
    });
    await render(hbs`{{rsa-page-layout/rsa-header-with-close closeAction=closeAction}}`);
    await click('.close-zone .rsa-icon-close-filled');
  });
});
